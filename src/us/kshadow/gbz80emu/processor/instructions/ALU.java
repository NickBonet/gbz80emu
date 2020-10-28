package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import static us.kshadow.gbz80emu.util.BitUtil.*;

import us.kshadow.gbz80emu.memory.MMU;

/**
 * Instructions pertaining to the Arithmetic Logic Unit for the CPU.
 * @author Nicholas Bonet
 */

public class ALU {	
	
	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final FlagRegister fr = reg.getFR();
	private static final MMU mmu = MMU.getInstance();
	
	private ALU() {}
	// TODO: Implement DAA instruction.
	
	// Misc. instructions.
	
	/**
	 * SCF - Set carry flag, reset N and H
	 */
	public static void instructSCF() {
		fr.setN(false);
		fr.setH(false);
		fr.setC(true);
	}
	
	/**
	 * CPL - Complement of register A. (Flip all bits)
	 */
	public static void instructCPL() {
		int result = reg.read("A") ^ 0xFF;
		fr.setN(true);
		fr.setH(true);
		reg.write("A", result);
	}
	
	/**
	 * CCF - Complement carry flag. (Flip from current value)
	 */
	public static void instructCCF() {
		fr.setN(false);
		fr.setH(false);
		fr.setC(!fr.isC());
	}
	
	// 8-bit Arithmetic.
	
	/**
	 * OR - Logically OR value with register A, and store result in A.
	 * @param arg - Value to OR with register A.
	 */
	public static void instructOR(int arg) {
		int result = reg.read("A") | arg;
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
		reg.write("A", result);
	}
	
	/**
	 * XOR - Logically XOR value with register A, and store result in A.
	 * @param arg - Value to XOR with register A.
	 */
	public static void instructXOR(int arg) {
		int result = reg.read("A") ^ arg;
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
		reg.write("A", result);
	}
	
	/**
	 * AND - Logically AND value with register A, and store result in A.
	 * @param arg - Value to AND with register A.
	 */
	public static void instructAND(int arg) {
		int result = reg.read("A") & arg;
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(true);
		reg.write("A", result);
	}
	
	/**
	 * INC - Increments given register.
	 * @param register - Register to increment.
	 */
	public static void instructINCu8(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = (regVal + 1) & 0xFF; // mask off higher than 8 bits if addition carries that much
		fr.setZ(result == 0);
		fr.setN(false);
		fr.setH(checkHalfCarryAdd(regVal, 1, false));
		writeValue(register, result);
	}
	
	/**
	 * DEC - Decrements given register.
	 * @param register - Register to decrement.
	 */
	public static void instructDECu8(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = (regVal - 1) & 0xFF; // two's complement if number reaches negative
		fr.setZ(result == 0);
		fr.setN(true);
		fr.setH(checkHalfCarrySub(regVal, 1, false));
		writeValue(register, result);
	}
	
	/**
	 * ADD - Adds the specified value to register A.
	 * @param arg - Value to be added to register A.
	 */
	public static void instructADD(int arg) {
		int result = (reg.read("A") + arg) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(false);
		fr.setH(checkHalfCarryAdd(reg.read("A"), arg, false));
		fr.setC(checkCarryAdd(reg.read("A"), arg, false));
		reg.write("A", result);
	}
	
	/**
	 * ADC - Adds specified value and the carry flag value to register A.
	 * @param arg - Value to be added to register A.
	 */
	public static void instructADC(int arg) {
		int result = (reg.read("A") + arg + (fr.isC() ? 1 : 0)) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(false);
		fr.setH(checkHalfCarryAdd(reg.read("A"), arg, fr.isC()));
		fr.setC(checkCarryAdd(reg.read("A"), arg, fr.isC()));
		reg.write("A", result);
	}
	
	/**
	 * SUB/CP - Subtracts specified value from register A.
	 * @param arg - Value to subtract from register A.
	 * @param cp - If true, treat instruction as CP and don't save result in A. (Only difference between SUB and CP)
	 */
	public static void instructSUB(int arg, boolean cp) {
		int result = (reg.read("A") - arg) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(true);
		fr.setH(checkHalfCarrySub(reg.read("A"), arg, false));
		fr.setC(checkCarrySub(reg.read("A"), arg, false));
		if (!cp) { reg.write("A", result); }
	}
	
	/**
	 * SBC - Subtracts specified value and the carry flag value from register A.
	 * @param arg - Value to be subtracted from register A.
	 */
	public static void instructSBC(int arg) {
		int result = (reg.read("A") - arg - (fr.isC() ? 1 : 0)) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(true);
		fr.setH(checkHalfCarrySub(reg.read("A"), arg, fr.isC()));
		fr.setC(checkCarrySub(reg.read("A"), arg, fr.isC()));
		reg.write("A", result);
	}
	
	// 16-Bit Arithmetic
	
	/**
	 * ADD u16 - Adds register to register HL.
	 * @param register - Register to add to register HL.
	 */
	public static void instructADDu16(String register) {
		int value = reg.read(register);
		int hl = reg.read("HL");
		int result = (value + hl) & 0xFFFF;
		fr.setN(false);
		fr.setC(hl + value > 0xFFFF);
		fr.setH((hl & 0x0fff) + (value & 0x0fff) > 0x0fff);
		reg.write("HL", result);
	}
	
	/**
	 * ADD SP, value - Adds value to Stack Pointer.
	 * @param value - Signed value to be added to SP.
	 */
	public static void instructADDSP(byte value) {
		int sp = reg.read("SP");
		int result = (sp + value) & 0xFFFF; 
		fr.setZ(false);
		fr.setN(false);
		fr.setC(((sp ^ value ^ result) & 0x100) == 0x100);
		fr.setH(((sp ^ value ^ result) & 0x10) == 0x10);
		reg.write("SP", result);
	}
	
	/**
	 * INC u16 - Increments given 16-bit register.
	 * @param register - Register to be incremented.
	 */
	public static void instructINCu16(String register) {
		int regVal = reg.read(register);
		int result = (regVal + 1) & 0xFFFF;
		reg.write(register, result);
	}
	
	/**
	 * DEC u16 - Decrements given 16-bit register.
	 * @param register - Register to be decremented.
	 */
	public static void instructDECu16(String register) {
		int regVal = reg.read(register);
		int result = (regVal - 1) & 0xFFFF;
		reg.write(register, result);
	}
	
	/**
	 * Writes changed value to its proper register or address in memory.
	 * @param register - register/pointer to write to.
	 * @param result - Value to write to register/pointer.
	 */
	private static void writeValue(String register, int result) {
		if(register.equals("HL")) {
			mmu.writeByte(reg.read("HL"), result);
		} else {
			reg.write(register, result);
		}
	}
}
