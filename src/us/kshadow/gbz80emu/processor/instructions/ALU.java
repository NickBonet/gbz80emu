package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import static us.kshadow.gbz80emu.util.BitUtil.*;

/**
 * Instructions pertaining to the Arithmetic Logic Unit for the CPU.
 * @author Nicholas Bonet
 */

public class ALU {	
	
	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final FlagRegister fr = cpuReg.getFR();
	
	private ALU() {}
	
	// Base instruction implementations. Will be mapped to opcodes later on.
	
	public static void instructOR(int arg) {
		int result = cpuReg.getReg("A") | arg;
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instructXOR(int arg) {
		int result = cpuReg.getReg("A") ^ arg;
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instructAND(int arg) {
		int result = cpuReg.getReg("A") & arg;
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(true);
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instructINCu8(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal + 1) & 0xFF; // mask off higher than 8 bits if addition carries that much
		fr.setZ(result == 0);
		fr.setN(false);
		fr.setH(checkHalfCarryAdd(regVal, 1, false));
		cpuReg.writeReg(register, result, false);
	}
	
	public static void instructADDu16(String register) {
		int value = cpuReg.getReg(register);
		int hl = cpuReg.getReg("HL");
		int result = (value + hl) & 0xFFFF;
		fr.setN(false);
		fr.setC(hl + value > 0xFFFF);
		fr.setH((hl & 0x0fff) + (value & 0x0fff) > 0x0fff);
		cpuReg.writeReg("HL", result, true);
	}
	
	public static void instructADDSP(byte value) {
		int sp = cpuReg.getReg("SP");
		int result = (sp + value) & 0xFFFF; 
		fr.setZ(false);
		fr.setN(false);
		fr.setC(((sp ^ value ^ result) & 0x100) == 0x100);
		fr.setH(((sp ^ value ^ result) & 0x10) == 0x10);
		cpuReg.writeReg("SP", result, true);
	}
	
	public static void instructINCu16(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal + 1) & 0xFFFF;
		cpuReg.writeReg(register, result, true);
	}
	
	public static void instructDECu16(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal - 1) & 0xFFFF;
		cpuReg.writeReg(register, result, true);
	}
	
	public static void instructDECu8(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal - 1) & 0xFF; // two's complement if number reaches negative
		fr.setZ(result == 0);
		fr.setN(true);
		fr.setH(checkHalfCarrySub(regVal, 1, false));
		cpuReg.writeReg(register, result, false);
	}
	
	// SCF -- Set carry flag, reset N and H
	public static void instructSCF() {
		fr.setN(false);
		fr.setH(false);
		fr.setC(true);
	}
	
	// CPL - flip bits of A
	public static void instructCPL() {
		int result = cpuReg.getReg("A") ^ 0xFF;
		fr.setN(true);
		fr.setH(true);
		cpuReg.writeReg("A", result, false);
	}
	
	// CCF - complement carry flag (flip it from current value)
	public static void instructCCF() {
		fr.setN(false);
		fr.setH(false);
		fr.setC(!fr.isC());
	}
	
	public static void instructADD(int arg) {
		int result = (cpuReg.getReg("A") + arg) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(false);
		fr.setH(checkHalfCarryAdd(cpuReg.getReg("A"), arg, false));
		fr.setC(checkCarryAdd(cpuReg.getReg("A"), arg, false));
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instructADC(int arg) {
		int result = (cpuReg.getReg("A") + arg + (fr.isC() ? 1 : 0)) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(false);
		fr.setH(checkHalfCarryAdd(cpuReg.getReg("A"), arg, fr.isC()));
		fr.setC(checkCarryAdd(cpuReg.getReg("A"), arg, fr.isC()));
		cpuReg.writeReg("A", result, false);
	}
	
	/**
	 *  @param cp - If true, treat instruction as CP and don't save result in A. (Only different between SUB and CP)
	 */
	public static void instructSUB(int arg, boolean cp) {
		int result = (cpuReg.getReg("A") - arg) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(true);
		fr.setH(checkHalfCarrySub(cpuReg.getReg("A"), arg, false));
		fr.setC(checkCarrySub(cpuReg.getReg("A"), arg, false));
		if (!cp) { cpuReg.writeReg("A", result, false); }
	}
	
	public static void instructSBC(int arg) {
		int result = (cpuReg.getReg("A") - arg - (fr.isC() ? 1 : 0)) & 0xFF;
		fr.setZ(result == 0);
		fr.setN(true);
		fr.setH(checkHalfCarrySub(cpuReg.getReg("A"), arg, fr.isC()));
		fr.setC(checkCarrySub(cpuReg.getReg("A"), arg, fr.isC()));
		cpuReg.writeReg("A", result, false);
	}
}
