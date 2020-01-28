package us.kshadow.gbz80emu.processor;

/* Main logic for modified Z80 implementation from the original Gameboy.
 * @author Nicholas Bonet
 */

public class CPU {

	private final CPURegisters cpuRegisters;
	
	public CPU() {
		this.cpuRegisters = new CPURegisters();
	}
	
	public CPURegisters getCpuRegisters() {
		return cpuRegisters;
	}
}
