package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import static us.kshadow.gbz80emu.util.BitUtil.*;

/**
 * Instructions pertaining to the Arithmetic Logic Unit for the CPU.
 * @author Nicholas Bonet
 */

public class ALU {	
	private static CPURegisters cpuReg = CPURegisters.getInstance();
	private static FlagRegister FR = cpuReg.getFR();
	
	// Base instruction implementations. Will be mapped to opcodes later on.
	
	public static void instruct_OR(int arg) {
		int result = cpuReg.getReg("A") | arg;
		FR.setZ(result == 0);
		FR.setC(false);
		FR.setN(false);
		FR.setH(false);
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instruct_XOR(int arg) {
		int result = cpuReg.getReg("A") ^ arg;
		FR.setZ(result == 0);
		FR.setC(false);
		FR.setN(false);
		FR.setH(false);
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instruct_AND(int arg) {
		int result = cpuReg.getReg("A") & arg;
		FR.setZ(result == 0);
		FR.setC(false);
		FR.setN(false);
		FR.setH(true);
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instruct_INC_u8(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal + 1) & 0xFF; // mask off higher than 8 bits if addition carries that much
		FR.setZ(result == 0);
		FR.setN(false);
		FR.setH(checkHalfCarryAdd(regVal, 1, false));
		cpuReg.writeReg(register, result, false);
	}
	
	public static void instruct_INC_u16(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal + 1) & 0xFFFF;
		cpuReg.writeReg(register, result, true);
	}
	
	public static void instruct_DEC_u16(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal - 1) & 0xFFFF;
		cpuReg.writeReg(register, result, true);
	}
	
	public static void instruct_DEC_u8(String register) {
		int regVal = cpuReg.getReg(register);
		int result = (regVal - 1) & 0xFF; // two's complement if number reaches negative
		FR.setZ(result == 0);
		FR.setN(true);
		FR.setH(checkHalfCarrySub(regVal, 1, false));
		cpuReg.writeReg(register, result, false);
	}
	
	// SCF -- Set carry flag, reset N and H
	public static void instruct_SCF() {
		FR.setN(false);
		FR.setH(false);
		FR.setC(true);
	}
	
	// CPL - flip bits of A
	public static void instruct_CPL() {
		int result = cpuReg.getReg("A") ^ 0xFF;
		FR.setN(true);
		FR.setH(true);
		cpuReg.writeReg("A", result, false);
	}
	
	// CCF - complement carry flag (flip it from current value)
	public static void instruct_CCF() {
		FR.setN(false);
		FR.setH(false);
		FR.setC(!FR.isC());
	}
	
	public static void instruct_ADD(int arg) {
		int result = (cpuReg.getReg("A") + arg) & 0xFF;
		FR.setZ(result == 0);
		FR.setN(false);
		FR.setH(checkHalfCarryAdd(cpuReg.getReg("A"), arg, false));
		FR.setC(checkCarryAdd(cpuReg.getReg("A"), arg, false));
		cpuReg.writeReg("A", result, false);
	}
	
	public static void instruct_ADC(int arg) {
		int result = (cpuReg.getReg("A") + arg + (FR.isC() ? 1 : 0)) & 0xFF;
		FR.setZ(result == 0);
		FR.setN(false);
		FR.setH(checkHalfCarryAdd(cpuReg.getReg("A"), arg, FR.isC()));
		FR.setC(checkCarryAdd(cpuReg.getReg("A"), arg, FR.isC()));
		cpuReg.writeReg("A", result, false);
	}
	
	/**
	 *  @param CP - If true, treat instruction as CP and don't save result in A. (Only different between SUB and CP)
	 */
	public static void instruct_SUB(int arg, boolean CP) {
		int result = (cpuReg.getReg("A") - arg) & 0xFF;
		FR.setZ(result == 0);
		FR.setN(true);
		FR.setH(checkHalfCarrySub(cpuReg.getReg("A"), arg, false));
		FR.setC(checkCarrySub(cpuReg.getReg("A"), arg, false));
		if (!CP) { cpuReg.writeReg("A", result, false); }
	}
	
	public static void instruct_SBC(int arg) {
		int result = (cpuReg.getReg("A") - arg - (FR.isC() ? 1 : 0)) & 0xFF;
		FR.setZ(result == 0);
		FR.setN(true);
		FR.setH(checkHalfCarrySub(cpuReg.getReg("A"), arg, FR.isC()));
		FR.setC(checkCarrySub(cpuReg.getReg("A"), arg, FR.isC()));
		cpuReg.writeReg("A", result, false);
	}
}
