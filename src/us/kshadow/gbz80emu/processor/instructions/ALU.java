package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;

/*
 * Instructions pertaining to the Arithmetic Logic Unit for the CPU.
 * @author Nicholas Bonet
 */

public class ALU {	
	private static CPURegisters cpuRegisters = CPURegisters.getInstance();
	
	// Base instruction implementations. Will be mapped to opcodes later on.
	
	public static void instruct_OR(int arg) {
		int result = cpuRegisters.getA() | arg;
		cpuRegisters.getFR().setZ(result);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		cpuRegisters.setA(result);
	}
	
	public static void instruct_XOR(int arg) {
		int result = cpuRegisters.getA() ^ arg;
		cpuRegisters.getFR().setZ(result);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		cpuRegisters.setA(result);
	}
	
	public static void instruct_AND(int arg) {
		int result = cpuRegisters.getA() & arg;
		cpuRegisters.getFR().setZ(result);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(true);
		cpuRegisters.setA(result);
	}
	
}
