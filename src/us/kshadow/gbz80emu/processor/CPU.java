package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.memory.MMU;

/** 
 * Takes care of the actual fetch-decode-execute logic for the emulator.
 * @author Nicholas Bonet
 */

public class CPU {

	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	
	public CPU() {
		// TODO: To be implemented later in the project.
	}
	
	public void fetchInstruction() {
		int instruction = mmu.readByte(cpuReg.getReg("PC"));
		cpuReg.incPC();
		switch (instruction) {
		case 0x31: // LD SP, d16
			cpuReg.writeReg("SP", mmu.readWord(cpuReg.getPC()), true);
			cpuReg.incPC();
			cpuReg.incPC();
		default:
			break;
		}
	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
	
	public MMU getMMU() {
		return mmu;
	}
}
