package us.kshadow.gbz80emu.processor;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.instructions.ALU;
import us.kshadow.gbz80emu.processor.instructions.BitShift;
import us.kshadow.gbz80emu.processor.instructions.ControlFlow;
import us.kshadow.gbz80emu.util.BitUtil;

/** 
 * Takes care of the actual fetch-decode-execute logic for the emulator.
 * @author Nicholas Bonet
 */

public class CPU {

	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	private static final Logger logger = Logger.getLogger("GBZ80Emu");
	private int cpuCycles;
	private boolean isRunning;
	private boolean delayedEI;

	/**
	 * Initializer for the CPU object.
	 */
	public CPU() {
		cpuCycles = 0;
		isRunning = true;
		mmu.toggleBootROM(true);
		logger.log(Level.INFO, "CPU execution started.");
	}

	/**
	 * Handles checking for interrupts after normal GPU/CPU steps.
	 * @return The number of cycles taken (to pass off to GPU for timekeeping)
	 */
	public int handleInterrupt() {
		int cycles = 0;
		int interruptFlag = mmu.readByte(0xFF0F);
		int interruptEnable = mmu.readByte(0xFFFF);
		if (reg.getIME() && interruptEnable > 0 && interruptFlag > 0) {
			// Check if a VBlank interrupt occurred.
			if ((interruptFlag & interruptEnable) == 0x01) {
				reg.toggleIME(false);
				mmu.writeByte(0xFF0F, BitUtil.setBit(interruptFlag, 0));
				ControlFlow.instructPUSH("PC");
				reg.write("PC", 0x40);
				cycles += 20; // According to The Cycle Accurate GameBoy Docs
			}
			// Timer overflow interrupt (or should be)
			else if ((interruptFlag & interruptEnable) == 0x04) {
				reg.toggleIME(false);
				mmu.writeByte(0xFF0F, BitUtil.setBit(interruptFlag, 2));
				ControlFlow.instructPUSH("PC");
				reg.write("PC", 0x50);
				cycles += 20;
			}
		}
		cpuCycles += cycles;
		return cycles;
	}

	/**
	 * Fetches the next byte in memory and executes the associated instruction.
	 * @return The number of cycles for the executed instruction.
	 */
	@SuppressWarnings("java:S1479")
	public int nextInstruction() {
		if (delayedEI) {
			delayedEI = false;
			ControlFlow.instructEI();
		}
		int instruction = fetchNextByte();
		int cycles;
		switch (instruction) {
		case 0x00: // NOP
			cycles = 4;
			break;
		case 0x01: // LD BC, u16
			reg.write("BC", fetchNextWord());
			cycles = 12;
			break;
		case 0x02: // LD (BC),A
			mmu.writeByte(reg.read("BC"), reg.read("A"));
			cycles = 8;
			break;
		case 0x03: // INC BC
			ALU.instructINCu16("BC");
			cycles = 8;
			break;
		case 0x04: // INC B
			ALU.instructINCu8("B");
			cycles = 4;
			break;
		case 0x05: // DEC B
			ALU.instructDECu8("B");
			cycles = 4;
			break;
		case 0x06: // LD B, u8
			reg.write("B", fetchNextByte());
			cycles = 8;
			break;
		case 0x07: // RLCA
			BitShift.instructRLCA();
			cycles = 4;
			break;
		case 0x08: // LD (u16), SP
			mmu.writeWord(fetchNextWord(), reg.read("SP"));
			cycles = 20;
			break;
		case 0x09: // ADD HL, BC
			ALU.instructADDu16("BC");
			cycles = 8;
			break;
		case 0x0A: // LD A, (BC)
			reg.write("A", mmu.readByte(reg.read("BC")));
			cycles = 8;
			break;
		case 0x0B: // DEC BC
			ALU.instructDECu16("BC");
			cycles = 8;
			break;
		case 0x0C: // INC C
			ALU.instructINCu8("C");
			cycles = 4;
			break;
		case 0x0D: // DEC C
			ALU.instructDECu8("C");
			cycles = 4;
			break;
		case 0x0E: // LD C, u8
			reg.write("C", fetchNextByte());
			cycles = 8;
			break;
		case 0x0F: // RRCA
			BitShift.instructRRCA();
			cycles = 4;
			break;
		case 0x10: // STOP
			isRunning = false;
			cycles = 4;
			break;
		case 0x11: // LD DE, u16
			reg.write("DE", fetchNextWord());
			cycles = 12;
			break;
		case 0x12: // LD (DE), A
			mmu.writeByte(reg.read("DE"), reg.read("A"));
			cycles = 8;
			break;
		case 0x13: // INC DE
			ALU.instructINCu16("DE");
			cycles = 8;
			break;
		case 0x14: // INC D
			ALU.instructINCu8("D");
			cycles = 4;
			break;
		case 0x15: // DEC D
			ALU.instructDECu8("D");
			cycles = 4;
			break;
		case 0x16: // LD D, u8
			reg.write("D", fetchNextByte());
			cycles = 8;
			break;
		case 0x17: // RLA
			BitShift.instructRLA();
			cycles = 4;
			break;
		case 0x18: // JR s8
			ControlFlow.instructJR((byte) fetchNextByte());
			cycles = 12;
			break;
		case 0x19: // ADD HL, DE
			ALU.instructADDu16("DE");
			cycles = 8;
			break;
		case 0x1A: // LD A, (DE)
			reg.write("A", mmu.readByte(reg.read("DE")));
			cycles = 8;
			break;
		case 0x1B: // DEC DE
			ALU.instructDECu16("DE");
			cycles = 8;
			break;
		case 0x1C: // INC E
			ALU.instructINCu8("E");
			cycles = 4;
			break;
		case 0x1D: // DEC E
			ALU.instructDECu8("E");
			cycles = 4;
			break;
		case 0x1E: // LD E, u8
			reg.write("E", fetchNextByte());
			cycles = 8;
			break;
		case 0x1F: // RRA
			BitShift.instructRRA();
			cycles = 4;
			break;
		case 0x27: // DAA
			ALU.instructDAA();
			cycles = 4;
			break;
		case 0x20: // JR NZ,s8
		case 0x28: // JR Z, s8
		case 0x30: // JR NC,s8
		case 0x38: // JR C, s8
			cycles = ControlFlow.instructCondJR(instruction, (byte) fetchNextByte());
			break;
		case 0x21: // LD HL,u16
			reg.write("HL", fetchNextWord());
			cycles = 12;
			break;
		case 0x22: // LD (HL+), A
			mmu.writeByte(reg.read("HL"), reg.read("A"));
			reg.write("HL", reg.read("HL") + 1);
			cycles = 8;
			break;
		case 0x23: // INC HL
			ALU.instructINCu16("HL");
			cycles = 8;
			break;
		case 0x24: // INC H
			ALU.instructINCu8("H");
			cycles = 4;
			break;
		case 0x25: // DEC H
			ALU.instructDECu8("H");
			cycles = 4;
			break;
		case 0x26: // LD H, u8
			reg.write("H", fetchNextByte());
			cycles = 8;
			break;
		case 0x29: // ADD HL, HL
			ALU.instructADDu16("HL");
			cycles = 8;
			break;
		case 0x2A: // LD A, (HL+)
			reg.write("A", mmu.readByte(reg.read("HL")));
			reg.write("HL", reg.read("HL") + 1);
			cycles = 8;
			break;
		case 0x2B: // DEC HL
			ALU.instructDECu16("HL");
			cycles = 8;
			break;
		case 0x2C: // INC L
			ALU.instructINCu8("L");
			cycles = 4;
			break;
		case 0x2D: // DEC L
			ALU.instructDECu8("L");
			cycles = 4;
			break;
		case 0x2E: // LD L, u8
			reg.write("L", fetchNextByte());
			cycles = 8;
			break;
		case 0x2F: // CPL
			ALU.instructCPL();
			cycles = 4;
			break;
		case 0x31: // LD SP, d16
			reg.write("SP", fetchNextWord());
			cycles = 12;
			break;
		case 0x32: // LD (HL-), A
			mmu.writeByte(reg.read("HL"), reg.read("A"));
			reg.write("HL", reg.read("HL") - 1);
			cycles = 8;
			break;
		case 0x33: // INC SP
			ALU.instructINCu16("SP");
			cycles = 8;
			break;
		case 0x34: // INC (HL)
			ALU.instructINCu8("HL");
			cycles = 12;
			break;
		case 0x35: // DEC (HL)
			ALU.instructDECu8("HL");
			cycles = 12;
			break;
		case 0x36: // LD (HL), u8
			mmu.writeByte(reg.read("HL"), fetchNextByte());
			cycles = 12;
			break;
		case 0x37: // SCF
			ALU.instructSCF();
			cycles = 4;
			break;
		case 0x39: // ADD HL, SP
			ALU.instructADDu16("SP");
			cycles = 8;
			break;
		case 0x3A: // LD A, (HL-)
			reg.write("A", mmu.readByte(reg.read("HL")));
			reg.write("HL", reg.read("HL") - 1);
			cycles = 8;
			break;
		case 0x3B: // DEC SP
			ALU.instructDECu16("SP");
			cycles = 8;
			break;
		case 0x3C: // INC A
			ALU.instructINCu8("A");
			cycles = 4;
			break;
		case 0x3D: // DEC A
			ALU.instructDECu8("A");
			cycles = 4;
			break;
		case 0x3E: // LD A, u8
			reg.write("A", fetchNextByte());
			cycles = 8;
			break;
		case 0x3F: // CCF
			ALU.instructCCF();
			cycles = 4;
			break;
		case 0x40: // LD B, B
			reg.write("B", reg.read("B"));
			cycles = 4;
			break;
		case 0x41: // LD B, C
			reg.write("B", reg.read("C"));
			cycles = 4;
			break;
		case 0x42: // LD B, D
			reg.write("B", reg.read("D"));
			cycles = 4;
			break;
		case 0x43: // LD B, E
			reg.write("B", reg.read("E"));
			cycles = 4;
			break;
		case 0x44: // LD B, H
			reg.write("B", reg.read("H"));
			cycles = 4;
			break;
		case 0x45: // LD B, L
			reg.write("B", reg.read("L"));
			cycles = 4;
			break;
		case 0x46: // LD B, (HL)
			reg.write("B", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x47: // LD B, A
			reg.write("B", reg.read("A"));
			cycles = 4;
			break;
		case 0x48: // LD C, B
			reg.write("C", reg.read("B"));
			cycles = 4;
			break;
		case 0x49: // LD C, C
			reg.write("C", reg.read("C"));
			cycles = 4;
			break;
		case 0x4A: // LD C, D
			reg.write("C", reg.read("D"));
			cycles = 4;
			break;
		case 0x4B: // LD C, E
			reg.write("C", reg.read("E"));
			cycles = 4;
			break;
		case 0x4C: // LD C, H
			reg.write("C", reg.read("H"));
			cycles = 4;
			break;
		case 0x4D: // LD C, L
			reg.write("C", reg.read("L"));
			cycles = 4;
			break;
		case 0x4E: // LD C, (HL)
			reg.write("C", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x4F: // LD C, A
			reg.write("C", reg.read("A"));
			cycles = 4;
			break;
		case 0x50: // LD D, B
			reg.write("D", reg.read("B"));
			cycles = 4;
			break;
		case 0x51: // LD D, C
			reg.write("D", reg.read("C"));
			cycles = 4;
			break;
		case 0x52: // LD D, D
			reg.write("D", reg.read("D"));
			cycles = 4;
			break;
		case 0x53: // LD D, E
			reg.write("D", reg.read("E"));
			cycles = 4;
			break;
		case 0x54: // LD D, H
			reg.write("D", reg.read("H"));
			cycles = 4;
			break;
		case 0x55: // LD D, L
			reg.write("D", reg.read("L"));
			cycles = 4;
			break;
		case 0x56: // LD D, (HL)
			reg.write("D", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x57: // LD D, A
			reg.write("D", reg.read("A"));
			cycles = 4;
			break;
		case 0x58: // LD E, B
			reg.write("E", reg.read("B"));
			cycles = 4;
			break;
		case 0x59: // LD E, C
			reg.write("E", reg.read("C"));
			cycles = 4;
			break;
		case 0x5A: // LD E, D
			reg.write("E", reg.read("D"));
			cycles = 4;
			break;
		case 0x5B: // LD E, E
			reg.write("E", reg.read("E"));
			cycles = 4;
			break;
		case 0x5C: // LD E, H
			reg.write("E", reg.read("H"));
			cycles = 4;
			break;
		case 0x5D: // LD E, L
			reg.write("E", reg.read("L"));
			cycles = 4;
			break;
		case 0x5E: // LD E, (HL)
			reg.write("E", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x5F: // LD E, A
			reg.write("E", reg.read("A"));
			cycles = 4;
			break;
		case 0x60: // LD H, B
			reg.write("H", reg.read("B"));
			cycles = 4;
			break;
		case 0x61: // LD H, C
			reg.write("H", reg.read("C"));
			cycles = 4;
			break;
		case 0x62: // LD H, D
			reg.write("H", reg.read("D"));
			cycles = 4;
			break;
		case 0x63: // LD H, E
			reg.write("H", reg.read("E"));
			cycles = 4;
			break;
		case 0x64: // LD H, H
			reg.write("H", reg.read("H"));
			cycles = 4;
			break;
		case 0x65: // LD H, L
			reg.write("H", reg.read("L"));
			cycles = 4;
			break;
		case 0x66: // LD H, (HL)
			reg.write("H", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x67: // LD H, A
			reg.write("H", reg.read("A"));
			cycles = 4;
			break;
		case 0x68: // LD L, B
			reg.write("L", reg.read("B"));
			cycles = 4;
			break;
		case 0x69: // LD L, C
			reg.write("L", reg.read("C"));
			cycles = 4;
			break;
		case 0x6A: // LD L, D
			reg.write("L", reg.read("D"));
			cycles = 4;
			break;
		case 0x6B: // LD L, E
			reg.write("L", reg.read("E"));
			cycles = 4;
			break;
		case 0x6C: // LD L, H
			reg.write("L", reg.read("H"));
			cycles = 4;
			break;
		case 0x6D: // LD L, L
			reg.write("L", reg.read("L"));
			cycles = 4;
			break;
		case 0x6E: // LD L, (HL)
			reg.write("L", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x6F: // LD L, A
			reg.write("L", reg.read("A"));
			cycles = 4;
			break;
		case 0x70: // LD (HL), B
			mmu.writeByte(reg.read("HL"), reg.read("B"));
			cycles = 8;
			break;
		case 0x71: // LD (HL), C
			mmu.writeByte(reg.read("HL"), reg.read("C"));
			cycles = 8;
			break;
		case 0x72: // LD (HL), D
			mmu.writeByte(reg.read("HL"), reg.read("D"));
			cycles = 8;
			break;
		case 0x73: // LD (HL), E
			mmu.writeByte(reg.read("HL"), reg.read("E"));
			cycles = 8;
			break;
		case 0x74: // LD (HL), H
			mmu.writeByte(reg.read("HL"), reg.read("H"));
			cycles = 8;
			break;
		case 0x75: // LD (HL), L
			mmu.writeByte(reg.read("HL"), reg.read("L"));
			cycles = 8;
			break;
		case 0x76: // HALT
			// TODO: implement this instruction
			cycles = 4;
			break;
		case 0x77: // LD (HL), A
			mmu.writeByte(reg.read("HL"), reg.read("A"));
			cycles = 8;
			break;
		case 0x78: // LD A, B
			reg.write("A", reg.read("B"));
			cycles = 4;
			break;
		case 0x79: // LD A, C
			reg.write("A", reg.read("C"));
			cycles = 4;
			break;
		case 0x7A: // LD A, D
			reg.write("A", reg.read("D"));
			cycles = 4;
			break;
		case 0x7B: // LD A, E
			reg.write("A", reg.read("E"));
			cycles = 4;
			break;
		case 0x7C: // LD A, H
			reg.write("A", reg.read("H"));
			cycles = 4;
			break;
		case 0x7D: // LD A, L
			reg.write("A", reg.read("L"));
			cycles = 4;
			break;
		case 0x7E: // LD A, (HL)
			reg.write("A", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x7F: // LD A, A
			reg.write("A", reg.read("A"));
			cycles = 4;
			break;
		case 0x80: // ADD A, B
			ALU.instructADD(reg.read("B"));
			cycles = 4;
			break;
		case 0x81: // ADD A, C
			ALU.instructADD(reg.read("C"));
			cycles = 4;
			break;
		case 0x82: // ADD A, D
			ALU.instructADD(reg.read("D"));
			cycles = 4;
			break;
		case 0x83: // ADD A, E
			ALU.instructADD(reg.read("E"));
			cycles = 4;
			break;
		case 0x84: // ADD A, H
			ALU.instructADD(reg.read("H"));
			cycles = 4;
			break;
		case 0x85: // ADD A, L
			ALU.instructADD(reg.read("L"));
			cycles = 4;
			break;
		case 0x86: // ADD A, (HL)
			ALU.instructADD(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x87: // ADD A, A
			ALU.instructADD(reg.read("A"));
			cycles = 4;
			break;
		case 0x88: // ADC A, B
			ALU.instructADC(reg.read("B"));
			cycles = 4;
			break;
		case 0x89: // ADC A, C
			ALU.instructADC(reg.read("C"));
			cycles = 4;
			break;
		case 0x8A: // ADC A, D
			ALU.instructADC(reg.read("D"));
			cycles = 4;
			break;
		case 0x8B: // ADC A, E
			ALU.instructADC(reg.read("E"));
			cycles = 4;
			break;
		case 0x8C: // ADC A, H
			ALU.instructADC(reg.read("H"));
			cycles = 4;
			break;
		case 0x8D: // ADC A, L
			ALU.instructADC(reg.read("L"));
			cycles = 4;
			break;
		case 0x8E: // ADC A, (HL)
			ALU.instructADC(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x8F: // ADC A, A
			ALU.instructADC(reg.read("A"));
			cycles = 4;
			break;
		case 0x90: // SUB A, B
			ALU.instructSUB(reg.read("B"), false);
			cycles = 4;
			break;
		case 0x91: // SUB A, C
			ALU.instructSUB(reg.read("C"), false);
			cycles = 4;
			break;
		case 0x92: // SUB A, D
			ALU.instructSUB(reg.read("D"), false);
			cycles = 4;
			break;
		case 0x93: // SUB A, E
			ALU.instructSUB(reg.read("E"), false);
			cycles = 4;
			break;
		case 0x94: // SUB A, H
			ALU.instructSUB(reg.read("H"), false);
			cycles = 4;
			break;
		case 0x95: // SUB A, L
			ALU.instructSUB(reg.read("L"), false);
			cycles = 4;
			break;
		case 0x97: // SUB A, A
			ALU.instructSUB(reg.read("A"), false);
			cycles = 4;
			break;
		case 0x98: // SBC B
			ALU.instructSBC(reg.read("B"));
			cycles = 4;
			break;
		case 0x99: // SBC C
			ALU.instructSBC(reg.read("C"));
			cycles = 4;
			break;
		case 0x9A: // SBC D
			ALU.instructSBC(reg.read("D"));
			cycles = 4;
			break;
		case 0x9B: // SBC E
			ALU.instructSBC(reg.read("E"));
			cycles = 4;
			break;
		case 0x9C: // SBC H
			ALU.instructSBC(reg.read("H"));
			cycles = 4;
			break;
		case 0x9D: // SBC L
			ALU.instructSBC(reg.read("L"));
			cycles = 4;
			break;
		case 0x9F: // SBC A
			ALU.instructSBC(reg.read("A"));
			cycles = 4;
			break;
		case 0xA0: // AND B
			ALU.instructAND(reg.read("B"));
			cycles = 4;
			break;
		case 0xA1: // AND C
			ALU.instructAND(reg.read("C"));
			cycles = 4;
			break;
		case 0xA2: // AND D
			ALU.instructAND(reg.read("D"));
			cycles = 4;
			break;
		case 0xA3: // AND E
			ALU.instructAND(reg.read("E"));
			cycles = 4;
			break;
		case 0xA4: // AND H
			ALU.instructAND(reg.read("H"));
			cycles = 4;
			break;
		case 0xA5: // AND L
			ALU.instructAND(reg.read("L"));
			cycles = 4;
			break;
		case 0xA7: // AND A
			ALU.instructAND(reg.read("A"));
			cycles = 4;
			break;
		case 0xA8: // XOR A, B
			ALU.instructXOR(reg.read("B"));
			cycles = 4;
			break;
		case 0xAB: // XOR A, E
			ALU.instructXOR(reg.read("E"));
			cycles = 4;
			break;
		case 0xAC: // XOR A, H
			ALU.instructXOR(reg.read("H"));
			cycles = 4;
			break;
		case 0xAD: // XOR A, L
			ALU.instructXOR(reg.read("L"));
			cycles = 4;
			break;
		case 0xAE: // XOR A, (HL)
			ALU.instructXOR(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0xA9: // XOR A, C
			ALU.instructXOR(reg.read("C"));
			cycles = 4;
			break;
		case 0xAA: // XOR D
			ALU.instructXOR(reg.read("D"));
			cycles = 4;
			break;
		case 0xAF: // XOR A
			ALU.instructXOR(reg.read("A"));
			cycles = 4;
			break;
		case 0xB0: // OR A, B
			ALU.instructOR(reg.read("B"));
			cycles = 4;
			break;
		case 0xB1: // OR A, C
			ALU.instructOR(reg.read("C"));
			cycles = 4;
			break;
		case 0xB2: // OR A, D
			ALU.instructOR(reg.read("D"));
			cycles = 4;
			break;
		case 0xB3: // OR A, E
			ALU.instructOR(reg.read("E"));
			cycles = 4;
			break;
		case 0xB4: // OR A, H
			ALU.instructOR(reg.read("H"));
			cycles = 4;
			break;
		case 0xB5: // OR A, L
			ALU.instructOR(reg.read("L"));
			cycles = 4;
			break;
		case 0xB6: // OR A, (HL)
			ALU.instructOR(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0xB7: // OR A, A
			ALU.instructOR(reg.read("A"));
			cycles = 4;
			break;
		case 0xB8: // CP A, B
			ALU.instructSUB(reg.read("B"), true);
			cycles = 4;
			break;
		case 0xB9: // CP A, C
			ALU.instructSUB(reg.read("C"), true);
			cycles = 4;
			break;
		case 0xBA: // CP A, D
			ALU.instructSUB(reg.read("D"), true);
			cycles = 4;
			break;
		case 0xBB: // CP A, E
			ALU.instructSUB(reg.read("E"), true);
			cycles = 4;
			break;
		case 0xBC: // CP A, H
			ALU.instructSUB(reg.read("H"), true);
			cycles = 4;
			break;
		case 0xBD: // CP A, L
			ALU.instructSUB(reg.read("L"), true);
			cycles = 4;
			break;
		case 0xBE: // CP A, (HL)
			ALU.instructSUB(mmu.readByte(reg.read("HL")), true);
			cycles = 8;
			break;
		case 0xBF: // CP A, A
			ALU.instructSUB(reg.read("A"), true);
			cycles = 4;
			break;
		case 0xC1: // POP BC
			ControlFlow.instructPOP("BC");
			cycles = 12;
			break;
		case 0xC3: // JP u16
			ControlFlow.instructJP(fetchNextWord());
			cycles = 16;
			break;
		case 0xCC: // CALL Z, u16
		case 0xC4: // CALL NZ, u16
		case 0xD4: // CALL NC, u16
		case 0xDC: // CALL C, u16
			cycles = ControlFlow.instructCondCALL(instruction, fetchNextWord());
			break;
		case 0xC5: // PUSH BC
			ControlFlow.instructPUSH("BC");
			cycles = 16;
			break;
		case 0xC6: // ADD A, u8
			ALU.instructADD(fetchNextByte());
			cycles = 8;
			break;
		case 0xC7: // RST 00
		case 0xCF: // RST 08
		case 0xD7: // RST 10
		case 0xDF: // RST 18
		case 0xE7: // RST 20
		case 0xEF: // RST 28
		case 0xF7: // RST 30
		case 0xFF :// RST 38
			ControlFlow.instructRST(instruction);
			cycles = 16;
			break;
		case 0xC9: // RET
			ControlFlow.instructRET();
			cycles = 16;
			break;
		case 0xC2: // JP NZ, u16
		case 0xCA: // JP Z, u16
		case 0xD2: // JP NC, u16
		case 0xDA: // JP C, u16
			cycles = ControlFlow.instructCondJP(instruction, fetchNextWord());
			break;
		case 0xCB: // send to CB handling function
			cycles = nextCBInstruction();
			break;
		case 0xCD: // CALL u16
			ControlFlow.instructCALL(fetchNextWord());
			cycles = 24;
			break;
		case 0xCE: // ADC A, u8
			ALU.instructADC(fetchNextByte());
			cycles = 8;
			break;
		case 0xC8: // RET Z
		case 0xC0: // RET NZ
		case 0xD8: // RET C
		case 0xD0: // RET NC
			cycles = ControlFlow.instructCondRET(instruction);
			break;
		case 0xD1: // POP DE
			ControlFlow.instructPOP("DE");
			cycles = 12;
			break;
		case 0xD5: // PUSH DE
			ControlFlow.instructPUSH("DE");
			cycles = 16;
			break;
		case 0xD6: // SUB A, u8
			ALU.instructSUB(fetchNextByte(), false);
			cycles = 8;
			break;
		case 0xD9: // RETI
			ControlFlow.instructRETI();
			cycles = 16;
			break;
		case 0xDE: // SBC A, u8
			ALU.instructSBC(fetchNextByte());
			cycles = 8;
			break;
		case 0xE0: // LD ($FF00+n), A
			mmu.writeByte(0xFF00 + fetchNextByte(), reg.read("A"));
			cycles = 12;
			break;
		case 0xE1: // POP HL
			ControlFlow.instructPOP("HL");
			cycles = 12;
			break;
		case 0xE2: // LD ($FF00+C), A
			mmu.writeByte(0xFF00 + reg.read("C"), reg.read("A"));
			cycles = 8;
			break;
		case 0xE5: // PUSH HL
			ControlFlow.instructPUSH("HL");
			cycles = 16;
			break;
		case 0xE6: // AND A, u8
			ALU.instructAND(fetchNextByte());
			cycles = 8;
			break;
		case 0xE8: // ADD SP, s8
			ALU.instructADDSP((byte) fetchNextByte());
			cycles = 16;
			break;
		case 0xE9: // JP HL
			ControlFlow.instructJP(reg.read("HL"));
			cycles = 4;
			break;
		case 0xEA: // LD (u16), A
			mmu.writeByte(fetchNextWord(), reg.read("A"));
			cycles = 16;
			break;
		case 0xEE: // XOR A, u8
			ALU.instructXOR(fetchNextByte());
			cycles = 8;
			break;
		case 0xF0: // LD A, ($FF00+n)
			reg.write("A", mmu.readByte(0xFF00 + fetchNextByte()));
			cycles = 12;
			break;
		case 0xF1: // POP AF
			ControlFlow.instructPOP("AF");
			cycles = 12;
			break;
		case 0xF2: // LD A, (FF00+C)
			reg.write("A", mmu.readByte(reg.read("C") + 0xFF00));
			cycles = 8;
			break;
		case 0xF3: // DI
			ControlFlow.instructDI();
			cycles = 4;
			break;
		case 0xF5: // PUSH AF
			ControlFlow.instructPUSH("AF");
			cycles = 16;
			break;
		case 0xF6: // OR A, u8
			ALU.instructOR(fetchNextByte());
			cycles = 8;
			break;
		case 0xF8: // LD HL, SP+s8
			byte value = (byte) fetchNextByte();
			int sp = reg.read("SP");
			int result = (sp + value) & 0xFFFF;
			reg.write("HL", result);
			reg.getFR().setZ(false);
			reg.getFR().setN(false);
			reg.getFR().setC(((sp ^ value ^ result) & 0x100) == 0x100);
			reg.getFR().setH(((sp ^ value ^ result) & 0x10) == 0x10);
			cycles = 12;
			break;
		case 0xF9: // LD SP, HL
			reg.write("SP", reg.read("HL"));
			cycles = 8;
			break;
		case 0xFA: // LD A, (u16)
			reg.write("A", mmu.readByte(fetchNextWord()));
			cycles = 16;
			break;
		case 0xFB: // EI
			delayedEI = true;
			cycles = 4;
			break;
		case 0xFE: // CP A, u8
			ALU.instructSUB(fetchNextByte(), true);
			cycles = 8;
			break;
		default:
			throw new IllegalArgumentException(String.format("Unhandled CPU instruction 0x%X", instruction));
		}
		cpuCycles += cycles;
		return cycles;
	}

	/**
	 * Fetches the next byte in memory and executes the associated CB instruction.
	 * @return The number of cycles for the executed CB instruction.
	 */
	@SuppressWarnings("java:S1479")
	private int nextCBInstruction() {
		int instruction = fetchNextByte();
		int cycles;
		switch(instruction) {
		case 0x00: // RLC B
			BitShift.instructRLC("B");
			cycles = 8;
			break;
		case 0x01: // RLC C
			BitShift.instructRLC("C");
			cycles = 8;
			break;
		case 0x02: // RLC D
			BitShift.instructRLC("D");
			cycles = 8;
			break;
		case 0x03: // RLC E
			BitShift.instructRLC("E");
			cycles = 8;
			break;
		case 0x04: // RLC H
			BitShift.instructRLC("H");
			cycles = 8;
			break;
		case 0x05: // RLC L
			BitShift.instructRLC("L");
			cycles = 8;
			break;
		case 0x06: // RLC (HL)
			BitShift.instructRLC("HL");
			cycles = 16;
			break;
		case 0x07: // RLC A
			BitShift.instructRLC("A");
			cycles = 8;
			break;
		case 0x08: // RRC B
			BitShift.instructRRC("B");
			cycles = 8;
			break;
		case 0x09: // RRC C
			BitShift.instructRRC("C");
			cycles = 8;
			break;
		case 0x0A: // RRC D
			BitShift.instructRRC("D");
			cycles = 8;
			break;
		case 0x0B: // RRC E
			BitShift.instructRRC("E");
			cycles = 8;
			break;
		case 0x0C: // RRC H
			BitShift.instructRRC("H");
			cycles = 8;
			break;
		case 0x0D: // RRC L
			BitShift.instructRRC("L");
			cycles = 8;
			break;
		case 0x0E: // RRC (HL)
			BitShift.instructRRC("HL");
			cycles = 16;
			break;
		case 0x0F: // RRC A
			BitShift.instructRRC("A");
			cycles = 8;
			break;
		case 0x10: // RL B
			BitShift.instructRL("B");
			cycles = 8;
			break;
		case 0x11: // RL C
			BitShift.instructRL("C");
			cycles = 8;
			break;
		case 0x12: // RL D
			BitShift.instructRL("D");
			cycles = 8;
			break;
		case 0x13: // RL E
			BitShift.instructRL("E");
			cycles = 8;
			break;
		case 0x14: // RL H
			BitShift.instructRL("H");
			cycles = 8;
			break;
		case 0x15: // RL L
			BitShift.instructRL("L");
			cycles = 8;
			break;
		case 0x16: // RL (HL)
			BitShift.instructRL("HL");
			cycles = 16;
			break;
		case 0x17: // RL A
			BitShift.instructRL("A");
			cycles = 8;
			break;
		case 0x18: // RR B
			BitShift.instructRR("B");
			cycles = 8;
			break;
		case 0x19: // RR C
			BitShift.instructRR("C");
			cycles = 8;
			break;
		case 0x1A: // RR D
			BitShift.instructRR("D");
			cycles = 8;
			break;
		case 0x1B: // RR E
			BitShift.instructRR("E");
			cycles = 8;
			break;
		case 0x1C: // RR H
			BitShift.instructRR("H");
			cycles = 8;
			break;
		case 0x1D: // RR L
			BitShift.instructRR("L");
			cycles = 8;
			break;
		case 0x1E: // RR (HL)
			BitShift.instructRR("E");
			cycles = 16;
			break;
		case 0x1F: // RR A
			BitShift.instructRR("A");
			cycles = 8;
			break;
		case 0x20: // SLA B
			BitShift.instructSLA("B");
			cycles = 8;
			break;
		case 0x21: // SLA C
			BitShift.instructSLA("C");
			cycles = 8;
			break;
		case 0x22: // SLA D
			BitShift.instructSLA("D");
			cycles = 8;
			break;
		case 0x23: // SLA E
			BitShift.instructSLA("E");
			cycles = 8;
			break;
		case 0x24: // SLA H
			BitShift.instructSLA("H");
			cycles = 8;
			break;
		case 0x25: // SLA L
			BitShift.instructSLA("L");
			cycles = 8;
			break;
		case 0x26: // SLA (HL)
			BitShift.instructSLA("E");
			cycles = 16;
			break;
		case 0x27: // SLA A
			BitShift.instructSLA("A");
			cycles = 8;
			break;
		case 0x28: // SRA B
			BitShift.instructSRA("B");
			cycles = 8;
			break;
		case 0x29: // SRA C
			BitShift.instructSRA("C");
			cycles = 8;
			break;
		case 0x2A: // SRA D
			BitShift.instructSRA("D");
			cycles = 8;
			break;
		case 0x2B: // SRA E
			BitShift.instructSRA("E");
			cycles = 8;
			break;
		case 0x2C: // SRA H
			BitShift.instructSRA("H");
			cycles = 8;
			break;
		case 0x2D: // SRA L
			BitShift.instructSRA("L");
			cycles = 8;
			break;
		case 0x2E: // SRA (HL)
			BitShift.instructSRA("E");
			cycles = 16;
			break;
		case 0x2F: // SRA A
			BitShift.instructSRA("A");
			cycles = 8;
			break;
		case 0x30: // SWAP B
			BitShift.instructSWAP("B");
			cycles = 8;
			break;
		case 0x31: // SWAP C
			BitShift.instructSWAP("C");
			cycles = 8;
			break;
		case 0x32: // SWAP D
			BitShift.instructSWAP("D");
			cycles = 8;
			break;
		case 0x33: // SWAP E
			BitShift.instructSWAP("E");
			cycles = 8;
			break;
		case 0x34: // SWAP H
			BitShift.instructSWAP("H");
			cycles = 8;
			break;
		case 0x35: // SWAP L
			BitShift.instructSWAP("L");
			cycles = 8;
			break;
		case 0x36: // SWAP (HL)
			BitShift.instructSWAP("HL");
			cycles = 16;
			break;
		case 0x37: // SWAP A
			BitShift.instructSWAP("A");
			cycles = 8;
			break;
		case 0x38: // SRL B
			BitShift.instructSRL("B");
			cycles = 8;
			break;
		case 0x39: // SRL C
			BitShift.instructSRL("C");
			cycles = 8;
			break;
		case 0x3A: // SRL D
			BitShift.instructSRL("D");
			cycles = 8;
			break;
		case 0x3B: // SRL E
			BitShift.instructSRL("E");
			cycles = 8;
			break;
		case 0x3C: // SRL H
			BitShift.instructSRL("H");
			cycles = 8;
			break;
		case 0x3D: // SRL L
			BitShift.instructSRL("L");
			cycles = 8;
			break;
		case 0x3E: // SRL (HL)
			BitShift.instructSRL("E");
			cycles = 16;
			break;
		case 0x3F: // SRL A
			BitShift.instructSRL("A");
			cycles = 8;
			break;
		case 0x42: // BIT 0, D
			BitShift.instructBIT("D", 0);
			cycles = 8;
			break;
		case 0x47: // BIT 0, A
			BitShift.instructBIT("A", 0);
			cycles = 8;
			break;
		case 0x4A: // BIT 1, D
			BitShift.instructBIT("D", 1);
			cycles = 8;
			break;
		case 0x4F: // BIT 1, A
			BitShift.instructBIT("A", 1);
			cycles = 8;
			break;
		case 0x57: // BIT 2, A
			BitShift.instructBIT("A", 2);
			cycles = 8;
			break;
		case 0x7C: // BIT 7,H
			BitShift.instructBIT("H", 7);
			cycles = 8;
			break;
		case 0x7F: // BIT 7, A
			BitShift.instructBIT("A", 7);
			cycles = 8;
			break;
		default:
			throw new IllegalArgumentException(String.format("Unhandled CB instruction 0x%X", instruction));
		}
		return cycles;
	}

	/**
	 * Fetches the next byte and increments PC by 1.
	 * @return The next byte in memory.
	 */
	public int fetchNextByte() {
		int result = mmu.readByte(reg.getPC());
		reg.incPC();
		return result;
	}

	/**
	 * Fetches the next 2 bytes and increments PC by 2.
	 * @return The next 2 bytes in memory.
	 */
	public int fetchNextWord() {
		int result = mmu.readWord(reg.getPC());
		reg.incPC();
		reg.incPC();
		return result;
	}
	
	public CPURegisters getRegisters() {
		return reg;
	}
	
	public MMU getMMU() {
		return mmu;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public int getCycles() {
		return cpuCycles;
	}

	public void resetCyclesAfterFrame() {
		this.cpuCycles -= 70224;
	}
}
