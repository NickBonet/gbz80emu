package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.instructions.ALU;

/** 
 * Takes care of the actual fetch-decode-execute logic for the emulator.
 * @author Nicholas Bonet
 */

public class CPU {

	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	private int cycles = 0;
	
	public CPU() {
		// TODO: To be implemented later in the project.
	}
	
	public void fetchInstruction() {
		int instruction = fetchNextByte();
		switch (instruction) {
		case 0x31: // LD SP, d16
			cpuReg.writeReg("SP", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0xAF:
			ALU.instructXOR(cpuReg.getReg("A"));
			cycles += 4;
			break;
		case 0x21:
			cpuReg.writeReg("HL", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0x32:
			mmu.writeByte(cpuReg.getReg("HL"), cpuReg.getReg("A"));
			cpuReg.writeReg("HL", cpuReg.getReg("HL") - 1, true);
			cycles += 8;
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
		cpuReg.writeReg("PC", cpuReg.getPC() + 2, false);
		return result;
	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
	
	public MMU getMMU() {
		return mmu;
	}
}
