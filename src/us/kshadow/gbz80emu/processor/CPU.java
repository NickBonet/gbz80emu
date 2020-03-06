package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.memory.MMU;

/** 
 * Main logic for modified Z80/8080 implementation from the original Gameboy.
 * @author Nicholas Bonet
 */

public class CPU {

	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	
	public CPU() {
		// TODO: To be implemented later in the project.
	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
}
