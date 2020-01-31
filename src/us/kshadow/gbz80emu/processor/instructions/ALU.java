package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.util.BitUtil;

/*
 * Instructions pertaining to the Arithmetic Logic Unit for the CPU.
 * @author Nicholas Bonet
 */

public class ALU {	
	private static CPURegisters cpuRegisters = CPURegisters.getInstance();
	
	// Base instruction implementations. Will be mapped to opcodes later on.
	
	public static void instruct_OR(int arg) {
		int result = cpuRegisters.getA() | arg;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		cpuRegisters.setA(result);
	}
	
	public static void instruct_XOR(int arg) {
		int result = cpuRegisters.getA() ^ arg;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		cpuRegisters.setA(result);
	}
	
	public static void instruct_AND(int arg) {
		int result = cpuRegisters.getA() & arg;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(true);
		cpuRegisters.setA(result);
	}
	
	public static void instruct_INC_u8(String register) {
		int regVal = cpuRegisters.getRegister(register);
		int result = (regVal + 1) & 0xFF; // mask off higher than 8 bits if addition carries that much
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarryAdd(regVal, 1, false));
		cpuRegisters.writeToRegister(register, result);
	}
	
	public static void instruct_INC_u16(String register) {
		int regVal = cpuRegisters.getRegister(register);
		int result = (regVal + 1) & 0xFFFF;
		cpuRegisters.writeToRegister(register, result);
	}
	
	public static void instruct_DEC_u16(String register) {
		int regVal = cpuRegisters.getRegister(register);
		int result = (regVal - 1) & 0xFFFF;
		cpuRegisters.writeToRegister(register, result);
	}
	
	public static void instruct_DEC_u8(String register) {
		int regVal = cpuRegisters.getRegister(register);
		int result = (regVal - 1) & 0xFF; // two's complement if number reaches negative
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(true);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarrySub(regVal, 1, false));
		cpuRegisters.writeToRegister(register, result);
	}
	
	// SCF -- Set carry flag, reset N and H
	public static void instruct_SCF() {
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		cpuRegisters.getFR().setC(true);
	}
	
	// CPL - flip bits of A
	public static void instruct_CPL() {
		int result = cpuRegisters.getA() ^ 0xFF;
		cpuRegisters.getFR().setN(true);
		cpuRegisters.getFR().setH(true);
		cpuRegisters.setA(result);
	}
	
	// CCF - complement carry flag (flip it from current value)
	public static void instruct_CCF() {
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		cpuRegisters.getFR().setC(!cpuRegisters.getFR().isC());
	}
	
	public static void instruct_ADD(int arg) {
		int result = (cpuRegisters.getA() + arg) & 0xFF;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarryAdd(cpuRegisters.getA(), arg, false));
		cpuRegisters.getFR().setC(BitUtil.checkCarryAdd(cpuRegisters.getA(), arg, false));
		cpuRegisters.setA(result);
	}
	
	public static void instruct_ADC(int arg) {
		int result = (cpuRegisters.getA() + arg + (cpuRegisters.getFR().isC() ? 1 : 0)) & 0xFF;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarryAdd(cpuRegisters.getA(), arg, cpuRegisters.getFR().isC()));
		cpuRegisters.getFR().setC(BitUtil.checkCarryAdd(cpuRegisters.getA(), arg, cpuRegisters.getFR().isC()));
		cpuRegisters.setA(result);
	}
	
	// @param CP - If true, treat instruction as CP and don't save result in A. (Only different between SUB and CP)
	public static void instruct_SUB(int arg, boolean CP) {
		int result = (cpuRegisters.getA() - arg) & 0xFF;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(true);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarrySub(cpuRegisters.getA(), arg, false));
		cpuRegisters.getFR().setC(BitUtil.checkCarrySub(cpuRegisters.getA(), arg, false));
		if (!CP) { cpuRegisters.setA(result); }
	}
	
	public static void instruct_SBC(int arg) {
		int result = (cpuRegisters.getA() - arg - (cpuRegisters.getFR().isC() ? 1 : 0)) & 0xFF;
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(true);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarrySub(cpuRegisters.getA(), arg, cpuRegisters.getFR().isC()));
		cpuRegisters.getFR().setC(BitUtil.checkCarrySub(cpuRegisters.getA(), arg, cpuRegisters.getFR().isC()));
		cpuRegisters.setA(result);
	}
}
