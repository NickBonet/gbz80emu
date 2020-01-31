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
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarryAdd(regVal, 1));
		cpuRegisters.writeToRegister(register, result);
	}
	
	public static void instruct_DEC_u8(String register) {
		int regVal = cpuRegisters.getRegister(register);
		int result = (regVal - 1) & 0xFF; // two's complement if number reaches negative
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setN(true);
		cpuRegisters.getFR().setH(BitUtil.checkHalfCarrySub(regVal, 1));
	}
}
