package us.kshadow.gbz80emu.processor;

/** 
 * Main logic for modified Z80/8080 implementation from the original Gameboy.
 * @author Nicholas Bonet
 */

public class CPU {

	private static CPURegisters cpuReg = CPURegisters.getInstance();
	
	public CPU() {
		// TODO: To be implemented later in the project.
	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
}
