package us.kshadow.gbz80emu.processor;

/** 
 * Main logic for modified Z80/8080 implementation from the original Gameboy.
 * @author Nicholas Bonet
 */

public class CPU {

	private CPURegisters cpuReg = CPURegisters.getInstance();
	
	public CPU() {

	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
}
