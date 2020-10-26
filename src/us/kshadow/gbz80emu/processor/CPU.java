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
		int instruction = fetchNextByte();
		switch (instruction) {
		case 0x31: // LD SP, d16
			cpuReg.writeReg("SP", fetchNextWord(), true);
			break;
		default:
			break;
		}
	}

	public int fetchNextByte() {
		int result = mmu.readByte(cpuReg.getPC());
		cpuReg.incPC();
		return result;
	}
	
	public int fetchNextWord() {
		int result = mmu.readWord(cpuReg.getPC());
		cpuReg.writeReg("PC", cpuReg.getPC() + 2, false);;
		return result;
	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
	
	public MMU getMMU() {
		return mmu;
	}
}
