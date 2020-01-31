package us.kshadow.gbz80emu.processor;

/* Main logic for modified Z80/8080 implementation from the original Gameboy.
 * @author Nicholas Bonet
 */

public class CPU {

	private CPURegisters cpuRegisters = CPURegisters.getInstance();
	
	public CPU() {

	}
	
	// For swapping upper/lower halves in 8-bit registers.
	public int instruct_SWAP(int register) {
		int result = ((register & 0x0F) << 4 | (register & 0xF0) >> 4);
		cpuRegisters.getFR().setZ(result == 0);
		cpuRegisters.getFR().setC(false);
		cpuRegisters.getFR().setN(false);
		cpuRegisters.getFR().setH(false);
		return result;
	}
	
	public CPURegisters getCpuRegisters() {
		return cpuRegisters;
	}
}
