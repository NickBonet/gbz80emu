package us.kshadow.gbz80emu.processor;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.instructions.ALU;
import us.kshadow.gbz80emu.processor.instructions.BitShift;
import us.kshadow.gbz80emu.processor.instructions.ControlFlow;

/** 
 * Takes care of the actual fetch-decode-execute logic for the emulator.
 * @author Nicholas Bonet
 */

public class CPU {

	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	private static final Logger logger = Logger.getLogger("GBZ80Emu");
	private int cycles = 0;
	
	public CPU() {
		// TODO: To be implemented later in the project.
	}
	
	public void fetchInstruction() {
		int instruction = fetchNextByte();
		String loggerMsg = String.format("Executing instruction 0x%x", instruction);
		logger.log(Level.INFO, loggerMsg);
		switch (instruction) {
		case 0x00: // NOP
			cycles += 4;
			break;
		case 0x0C: // INC C
			ALU.instructINCu8("C");
			cycles += 4;
			break;
		case 0x0E: // LD C, u8
			cpuReg.writeReg("C", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x20: // JR NZ,s8
			ControlFlow.instructCondJR(instruction, (byte) fetchNextByte());
			// TODO: figure out counting cycles on branches
			break;
		case 0x31: // LD SP, d16
			cpuReg.writeReg("SP", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0xAF: // XOR A
			ALU.instructXOR(cpuReg.getReg("A"));
			cycles += 4;
			break;
		case 0x21: // LD HL,u16
			cpuReg.writeReg("HL", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0x32: // LD (HL-),A
			mmu.writeByte(cpuReg.getReg("HL"), cpuReg.getReg("A"));
			cpuReg.writeReg("HL", cpuReg.getReg("HL") - 1, true);
			cycles += 8;
			break;
		case 0x3E: // LD A, u8
			cpuReg.writeReg("A", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x77: // LD (HL), A
			mmu.writeByte(cpuReg.getReg("HL"), cpuReg.getReg("A"));
			cycles += 8;
			break;
		case 0xCB: // send to CB handling function
			handleCBInstruction();
			break;
		case 0xE0: // LD ($FF00+n), A
			mmu.writeByte(0xFF00 + fetchNextByte(), cpuReg.getReg("A"));
			cycles += 8;
			break;
		case 0xE2: // LD ($FF00+C), A
			mmu.writeByte(0xFF00 + cpuReg.getReg("C"), cpuReg.getReg("A"));
			cycles += 8;
			break;
		default:
			throw new IllegalArgumentException("Unhandled CPU instruction!");
		}
	}
	
	private void handleCBInstruction() {
		int instruction = fetchNextByte();
		String loggerMsg = String.format("Executing CB instruction 0x%x", instruction);
		logger.log(Level.INFO, loggerMsg);
		switch(instruction) {
		case 0x7C: // BIT 7,H
			BitShift.instructBIT("H", 7);
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
		cpuReg.incPC();
		cpuReg.incPC();
		return result;
	}
	
	public CPURegisters getCpuReg() {
		return cpuReg;
	}
	
	public MMU getMMU() {
		return mmu;
	}
}
