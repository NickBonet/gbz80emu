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
		case 0x04: // INC B
			ALU.instructINCu8("B");
			cycles += 4;
			break;
		case 0x05: // DEC B
			ALU.instructDECu8("B");
			cycles += 4;
			break;
		case 0x06: // LD B, u8
			cpuReg.writeReg("B", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x0C: // INC C
			ALU.instructINCu8("C");
			cycles += 4;
			break;
		case 0x0D: // DEC C
			ALU.instructDECu8("C");
			cycles += 4;
			break;
		case 0x0E: // LD C, u8
			cpuReg.writeReg("C", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x11: // LD DE, u16
			cpuReg.writeReg("DE", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0x13: // INC DE
			ALU.instructINCu16("DE");
			cycles += 8;
			break;
		case 0x17: // RLA
			BitShift.instructRLA();
			cycles += 4;
			break;
		case 0x18: // JR s8
			ControlFlow.instructJR((byte) fetchNextByte());
			cycles += 12;
			break;
		case 0x1A: // LD A, (DE)
			cpuReg.writeReg("A", mmu.readByte(cpuReg.getReg("DE")), false);
			cycles += 8;
			break;
		case 0x1E: // LD E, u8
			cpuReg.writeReg("E", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x20: // JR NZ,s8
			ControlFlow.instructCondJR(instruction, (byte) fetchNextByte());
			// TODO: figure out counting cycles on branches
			break;
		case 0x21: // LD HL,u16
			cpuReg.writeReg("HL", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0x22: // LD (HL+), A
			mmu.writeByte(cpuReg.getReg("HL"), cpuReg.getReg("A"));
			cpuReg.writeReg("HL", cpuReg.getReg("HL") + 1, true);
			cycles += 8;
			break;
		case 0x23: // INC HL
			ALU.instructINCu16("HL");
			cycles += 8;
			break;
		case 0x28: // JR Z, s8
			ControlFlow.instructCondJR(instruction, (byte) fetchNextByte());
			// TODO: figure out counting cycles on branches
			break;
		case 0x2E: // LD L, u8
			cpuReg.writeReg("L", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x31: // LD SP, d16
			cpuReg.writeReg("SP", fetchNextWord(), true);
			cycles += 12;
			break;
		case 0x32: // LD (HL-), A
			mmu.writeByte(cpuReg.getReg("HL"), cpuReg.getReg("A"));
			cpuReg.writeReg("HL", cpuReg.getReg("HL") - 1, true);
			cycles += 8;
			break;
		case 0x3D: // DEC A
			ALU.instructDECu8("A");
			cycles += 4;
			break;
		case 0x3E: // LD A, u8
			cpuReg.writeReg("A", fetchNextByte(), false);
			cycles += 8;
			break;
		case 0x4F: // LD C, A
			cpuReg.writeReg("C", cpuReg.getReg("A"), false);
			cycles += 4;
			break;
		case 0x57: // LD D, A
			cpuReg.writeReg("D", cpuReg.getReg("A"), false);
			cycles += 4;
			break;
		case 0x67: // LD H, A
			cpuReg.writeReg("H", cpuReg.getReg("A"), false);
			cycles += 4;
			break;
		case 0x77: // LD (HL), A
			mmu.writeByte(cpuReg.getReg("HL"), cpuReg.getReg("A"));
			cycles += 8;
			break;
		case 0x7B: // LD A, E
			cpuReg.writeReg("A", cpuReg.getReg("E"), false);
			cycles += 4;
			break;
		case 0xAF: // XOR A
			ALU.instructXOR(cpuReg.getReg("A"));
			cycles += 4;
			break;
		case 0xC1: // POP BC
			ControlFlow.instructPOP("BC");
			cycles += 12;
			break;
		case 0xC5: // PUSH BC
			ControlFlow.instructPUSH("BC");
			cycles += 16;
			break;
		case 0xC9: // RET
			ControlFlow.instructRET();
			cycles += 16;
			break;
		case 0xCB: // send to CB handling function
			handleCBInstruction();
			break;
		case 0xCD: // CALL u16
			ControlFlow.instructCALL(fetchNextWord());
			cycles += 24;
			break;
		case 0xE0: // LD ($FF00+n), A
			mmu.writeByte(0xFF00 + fetchNextByte(), cpuReg.getReg("A"));
			cycles += 12;
			break;
		case 0xE2: // LD ($FF00+C), A
			mmu.writeByte(0xFF00 + cpuReg.getReg("C"), cpuReg.getReg("A"));
			cycles += 8;
			break;
		case 0xEA: // LD (u16), A
			mmu.writeByte(fetchNextWord(), cpuReg.getReg("A"));
			cycles += 16;
			break;
		case 0xF0: // LD A, ($FF00+n)
			cpuReg.writeReg("A", mmu.readByte(0xFF00 + fetchNextByte()), false);
			cycles += 12;
			break;
		case 0xFE:
			ALU.instructSUB(fetchNextByte(), true);
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
		case 0x11: // RL C
			BitShift.instructRL("C");
			cycles += 8;
			break;
		case 0x7C: // BIT 7,H
			BitShift.instructBIT("H", 7);
			cycles += 8;
			break;
		default:
			throw new IllegalArgumentException("Unhandled CPU CB instruction!");
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
