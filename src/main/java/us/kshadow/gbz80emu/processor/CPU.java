package us.kshadow.gbz80emu.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.instructions.ALU;
import us.kshadow.gbz80emu.processor.instructions.BitShift;
import us.kshadow.gbz80emu.processor.instructions.ControlFlow;
import us.kshadow.gbz80emu.util.BitUtil;

import static us.kshadow.gbz80emu.constants.MemoryAddresses.INTERRUPT_ENABLE;
import static us.kshadow.gbz80emu.constants.MemoryAddresses.INTERRUPT_FLAG;

/**
 * Takes care of the actual fetch-decode-execute logic for the emulator.
 * 
 * @author Nicholas Bonet
 */

public class CPU {

	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(CPU.class);
	private int cpuCycles;
	private boolean isRunning;
	private boolean delayedEI;
	private boolean isHalted;

	/**
	 * Initializer for the CPU object.
	 */
	public CPU() {
		cpuCycles = 0;
		isRunning = true;
		mmu.toggleBootROM(true);
		logger.debug("CPU execution started.");
	}

	/**
	 * Handles checking for interrupts after normal GPU/CPU steps.
	 * 
	 * @return The number of cycles taken (to pass off to GPU for timekeeping)
	 */
	public int handleInterrupt() {
		int cycles = 0;
		int interruptFlag = mmu.readByte(INTERRUPT_FLAG);
		int interruptEnable = mmu.readByte(INTERRUPT_ENABLE);
		if (reg.getIME() && interruptEnable > 0 && interruptFlag > 0) {
			// Check if a VBlank interrupt occurred.
			if ((interruptFlag & interruptEnable) == 0x01) {
				reg.toggleIME(false);
				mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 0));
				ControlFlow.instructPUSH("PC");
				reg.write("PC", 0x40);
				cycles += 20; // According to The Cycle Accurate Game Boy Docs
			}
			// Timer overflow interrupt
			else if ((interruptFlag & interruptEnable) == 0x04) {
				reg.toggleIME(false);
				mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 2));
				ControlFlow.instructPUSH("PC");
				reg.write("PC", 0x50);
				cycles += 20;
			}
			// Serial interrupt
			else if ((interruptFlag & interruptEnable) == 0x08) {
				reg.toggleIME(false);
				mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 3));
				ControlFlow.instructPUSH("PC");
				reg.write("PC", 0x58);
				cycles += 20;
			}
			// Joy pad interrupt
			else if ((interruptFlag & interruptEnable) == 0x10) {
				reg.toggleIME(false);
				mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 4));
				ControlFlow.instructPUSH("PC");
				reg.write("PC", 0x60);
				cycles += 20;
			}
		}

		if (isHalted && ((interruptFlag & interruptEnable) != 0)) {
			setHalted(false);
		}

		cpuCycles += cycles;
		return cycles;
	}

	/**
	 * Fetches the next byte in memory and executes the associated instruction.
	 * 
	 * @return The number of cycles for the executed instruction.
	 */
	@SuppressWarnings("java:S1479")
	public int nextInstruction() {
		if (!isHalted) {
			if (delayedEI) {
				delayedEI = false;
				ControlFlow.instructEI();
			}
			int instruction = fetchNextByte();
			int cycles;
			switch (instruction) {
				case 0x00 -> // NOP
					cycles = 4;
				case 0x01 -> { // LD BC, u16
					reg.write("BC", fetchNextWord());
					cycles = 12;
				}
				case 0x02 -> { // LD (BC),A
					mmu.writeByte(reg.read("BC"), reg.read("A"));
					cycles = 8;
				}
				case 0x03 -> { // INC BC
					ALU.instructINCu16("BC");
					cycles = 8;
				}
				case 0x04 -> { // INC B
					ALU.instructINCu8("B");
					cycles = 4;
				}
				case 0x05 -> { // DEC B
					ALU.instructDECu8("B");
					cycles = 4;
				}
				case 0x06 -> { // LD B, u8
					reg.write("B", fetchNextByte());
					cycles = 8;
				}
				case 0x07 -> { // RLCA
					BitShift.instructRLCA();
					cycles = 4;
				}
				case 0x08 -> { // LD (u16), SP
					mmu.writeWord(fetchNextWord(), reg.read("SP"));
					cycles = 20;
				}
				case 0x09 -> { // ADD HL, BC
					ALU.instructADDu16("BC");
					cycles = 8;
				}
				case 0x0A -> { // LD A, (BC)
					reg.write("A", mmu.readByte(reg.read("BC")));
					cycles = 8;
				}
				case 0x0B -> { // DEC BC
					ALU.instructDECu16("BC");
					cycles = 8;
				}
				case 0x0C -> { // INC C
					ALU.instructINCu8("C");
					cycles = 4;
				}
				case 0x0D -> { // DEC C
					ALU.instructDECu8("C");
					cycles = 4;
				}
				case 0x0E -> { // LD C, u8
					reg.write("C", fetchNextByte());
					cycles = 8;
				}
				case 0x0F -> { // RRCA
					BitShift.instructRRCA();
					cycles = 4;
				}
				case 0x10 -> { // STOP
					// TODO: basic impl, not supporting CGB speed switch currently
					isRunning = false;
					cycles = 4;
				}
				case 0x11 -> { // LD DE, u16
					reg.write("DE", fetchNextWord());
					cycles = 12;
				}
				case 0x12 -> { // LD (DE), A
					mmu.writeByte(reg.read("DE"), reg.read("A"));
					cycles = 8;
				}
				case 0x13 -> { // INC DE
					ALU.instructINCu16("DE");
					cycles = 8;
				}
				case 0x14 -> { // INC D
					ALU.instructINCu8("D");
					cycles = 4;
				}
				case 0x15 -> { // DEC D
					ALU.instructDECu8("D");
					cycles = 4;
				}
				case 0x16 -> { // LD D, u8
					reg.write("D", fetchNextByte());
					cycles = 8;
				}
				case 0x17 -> { // RLA
					BitShift.instructRLA();
					cycles = 4;
				}
				case 0x18 -> { // JR s8
					ControlFlow.instructJR((byte) fetchNextByte());
					cycles = 12;
				}
				case 0x19 -> { // ADD HL, DE
					ALU.instructADDu16("DE");
					cycles = 8;
				}
				case 0x1A -> { // LD A, (DE)
					reg.write("A", mmu.readByte(reg.read("DE")));
					cycles = 8;
				}
				case 0x1B -> { // DEC DE
					ALU.instructDECu16("DE");
					cycles = 8;
				}
				case 0x1C -> { // INC E
					ALU.instructINCu8("E");
					cycles = 4;
				}
				case 0x1D -> { // DEC E
					ALU.instructDECu8("E");
					cycles = 4;
				}
				case 0x1E -> { // LD E, u8
					reg.write("E", fetchNextByte());
					cycles = 8;
				}
				case 0x1F -> { // RRA
					BitShift.instructRRA();
					cycles = 4;
				}
				case 0x27 -> { // DAA
					ALU.instructDAA();
					cycles = 4;
				} // JR NZ,s8
					// JR Z, s8
					// JR NC,s8
				case 0x20, 0x28, 0x30, 0x38 -> // JR C, s8
					cycles = ControlFlow.instructCondJR(instruction, (byte) fetchNextByte());
				case 0x21 -> { // LD HL,u16
					reg.write("HL", fetchNextWord());
					cycles = 12;
				}
				case 0x22 -> { // LD (HL+), A
					mmu.writeByte(reg.read("HL"), reg.read("A"));
					ALU.instructINCu16("HL");
					cycles = 8;
				}
				case 0x23 -> { // INC HL
					ALU.instructINCu16("HL");
					cycles = 8;
				}
				case 0x24 -> { // INC H
					ALU.instructINCu8("H");
					cycles = 4;
				}
				case 0x25 -> { // DEC H
					ALU.instructDECu8("H");
					cycles = 4;
				}
				case 0x26 -> { // LD H, u8
					reg.write("H", fetchNextByte());
					cycles = 8;
				}
				case 0x29 -> { // ADD HL, HL
					ALU.instructADDu16("HL");
					cycles = 8;
				}
				case 0x2A -> { // LD A, (HL+)
					reg.write("A", mmu.readByte(reg.read("HL")));
					ALU.instructINCu16("HL");
					cycles = 8;
				}
				case 0x2B -> { // DEC HL
					ALU.instructDECu16("HL");
					cycles = 8;
				}
				case 0x2C -> { // INC L
					ALU.instructINCu8("L");
					cycles = 4;
				}
				case 0x2D -> { // DEC L
					ALU.instructDECu8("L");
					cycles = 4;
				}
				case 0x2E -> { // LD L, u8
					reg.write("L", fetchNextByte());
					cycles = 8;
				}
				case 0x2F -> { // CPL
					ALU.instructCPL();
					cycles = 4;
				}
				case 0x31 -> { // LD SP, d16
					reg.write("SP", fetchNextWord());
					cycles = 12;
				}
				case 0x32 -> { // LD (HL-), A
					mmu.writeByte(reg.read("HL"), reg.read("A"));
					ALU.instructDECu16("HL");
					cycles = 8;
				}
				case 0x33 -> { // INC SP
					ALU.instructINCu16("SP");
					cycles = 8;
				}
				case 0x34 -> { // INC (HL)
					ALU.instructINCu8("HL");
					cycles = 12;
				}
				case 0x35 -> { // DEC (HL)
					ALU.instructDECu8("HL");
					cycles = 12;
				}
				case 0x36 -> { // LD (HL), u8
					mmu.writeByte(reg.read("HL"), fetchNextByte());
					cycles = 12;
				}
				case 0x37 -> { // SCF
					ALU.instructSCF();
					cycles = 4;
				}
				case 0x39 -> { // ADD HL, SP
					ALU.instructADDu16("SP");
					cycles = 8;
				}
				case 0x3A -> { // LD A, (HL-)
					reg.write("A", mmu.readByte(reg.read("HL")));
					ALU.instructDECu16("HL");
					cycles = 8;
				}
				case 0x3B -> { // DEC SP
					ALU.instructDECu16("SP");
					cycles = 8;
				}
				case 0x3C -> { // INC A
					ALU.instructINCu8("A");
					cycles = 4;
				}
				case 0x3D -> { // DEC A
					ALU.instructDECu8("A");
					cycles = 4;
				}
				case 0x3E -> { // LD A, u8
					reg.write("A", fetchNextByte());
					cycles = 8;
				}
				case 0x3F -> { // CCF
					ALU.instructCCF();
					cycles = 4;
				}
				case 0x40 -> { // LD B, B
					reg.write("B", reg.read("B"));
					cycles = 4;
				}
				case 0x41 -> { // LD B, C
					reg.write("B", reg.read("C"));
					cycles = 4;
				}
				case 0x42 -> { // LD B, D
					reg.write("B", reg.read("D"));
					cycles = 4;
				}
				case 0x43 -> { // LD B, E
					reg.write("B", reg.read("E"));
					cycles = 4;
				}
				case 0x44 -> { // LD B, H
					reg.write("B", reg.read("H"));
					cycles = 4;
				}
				case 0x45 -> { // LD B, L
					reg.write("B", reg.read("L"));
					cycles = 4;
				}
				case 0x46 -> { // LD B, (HL)
					reg.write("B", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x47 -> { // LD B, A
					reg.write("B", reg.read("A"));
					cycles = 4;
				}
				case 0x48 -> { // LD C, B
					reg.write("C", reg.read("B"));
					cycles = 4;
				}
				case 0x49 -> { // LD C, C
					reg.write("C", reg.read("C"));
					cycles = 4;
				}
				case 0x4A -> { // LD C, D
					reg.write("C", reg.read("D"));
					cycles = 4;
				}
				case 0x4B -> { // LD C, E
					reg.write("C", reg.read("E"));
					cycles = 4;
				}
				case 0x4C -> { // LD C, H
					reg.write("C", reg.read("H"));
					cycles = 4;
				}
				case 0x4D -> { // LD C, L
					reg.write("C", reg.read("L"));
					cycles = 4;
				}
				case 0x4E -> { // LD C, (HL)
					reg.write("C", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x4F -> { // LD C, A
					reg.write("C", reg.read("A"));
					cycles = 4;
				}
				case 0x50 -> { // LD D, B
					reg.write("D", reg.read("B"));
					cycles = 4;
				}
				case 0x51 -> { // LD D, C
					reg.write("D", reg.read("C"));
					cycles = 4;
				}
				case 0x52 -> { // LD D, D
					reg.write("D", reg.read("D"));
					cycles = 4;
				}
				case 0x53 -> { // LD D, E
					reg.write("D", reg.read("E"));
					cycles = 4;
				}
				case 0x54 -> { // LD D, H
					reg.write("D", reg.read("H"));
					cycles = 4;
				}
				case 0x55 -> { // LD D, L
					reg.write("D", reg.read("L"));
					cycles = 4;
				}
				case 0x56 -> { // LD D, (HL)
					reg.write("D", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x57 -> { // LD D, A
					reg.write("D", reg.read("A"));
					cycles = 4;
				}
				case 0x58 -> { // LD E, B
					reg.write("E", reg.read("B"));
					cycles = 4;
				}
				case 0x59 -> { // LD E, C
					reg.write("E", reg.read("C"));
					cycles = 4;
				}
				case 0x5A -> { // LD E, D
					reg.write("E", reg.read("D"));
					cycles = 4;
				}
				case 0x5B -> { // LD E, E
					reg.write("E", reg.read("E"));
					cycles = 4;
				}
				case 0x5C -> { // LD E, H
					reg.write("E", reg.read("H"));
					cycles = 4;
				}
				case 0x5D -> { // LD E, L
					reg.write("E", reg.read("L"));
					cycles = 4;
				}
				case 0x5E -> { // LD E, (HL)
					reg.write("E", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x5F -> { // LD E, A
					reg.write("E", reg.read("A"));
					cycles = 4;
				}
				case 0x60 -> { // LD H, B
					reg.write("H", reg.read("B"));
					cycles = 4;
				}
				case 0x61 -> { // LD H, C
					reg.write("H", reg.read("C"));
					cycles = 4;
				}
				case 0x62 -> { // LD H, D
					reg.write("H", reg.read("D"));
					cycles = 4;
				}
				case 0x63 -> { // LD H, E
					reg.write("H", reg.read("E"));
					cycles = 4;
				}
				case 0x64 -> { // LD H, H
					reg.write("H", reg.read("H"));
					cycles = 4;
				}
				case 0x65 -> { // LD H, L
					reg.write("H", reg.read("L"));
					cycles = 4;
				}
				case 0x66 -> { // LD H, (HL)
					reg.write("H", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x67 -> { // LD H, A
					reg.write("H", reg.read("A"));
					cycles = 4;
				}
				case 0x68 -> { // LD L, B
					reg.write("L", reg.read("B"));
					cycles = 4;
				}
				case 0x69 -> { // LD L, C
					reg.write("L", reg.read("C"));
					cycles = 4;
				}
				case 0x6A -> { // LD L, D
					reg.write("L", reg.read("D"));
					cycles = 4;
				}
				case 0x6B -> { // LD L, E
					reg.write("L", reg.read("E"));
					cycles = 4;
				}
				case 0x6C -> { // LD L, H
					reg.write("L", reg.read("H"));
					cycles = 4;
				}
				case 0x6D -> { // LD L, L
					reg.write("L", reg.read("L"));
					cycles = 4;
				}
				case 0x6E -> { // LD L, (HL)
					reg.write("L", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x6F -> { // LD L, A
					reg.write("L", reg.read("A"));
					cycles = 4;
				}
				case 0x70 -> { // LD (HL), B
					mmu.writeByte(reg.read("HL"), reg.read("B"));
					cycles = 8;
				}
				case 0x71 -> { // LD (HL), C
					mmu.writeByte(reg.read("HL"), reg.read("C"));
					cycles = 8;
				}
				case 0x72 -> { // LD (HL), D
					mmu.writeByte(reg.read("HL"), reg.read("D"));
					cycles = 8;
				}
				case 0x73 -> { // LD (HL), E
					mmu.writeByte(reg.read("HL"), reg.read("E"));
					cycles = 8;
				}
				case 0x74 -> { // LD (HL), H
					mmu.writeByte(reg.read("HL"), reg.read("H"));
					cycles = 8;
				}
				case 0x75 -> { // LD (HL), L
					mmu.writeByte(reg.read("HL"), reg.read("L"));
					cycles = 8;
				}
				case 0x76 -> { // HALT
					isHalted = true;
					cycles = 4;
				}
				case 0x77 -> { // LD (HL), A
					mmu.writeByte(reg.read("HL"), reg.read("A"));
					cycles = 8;
				}
				case 0x78 -> { // LD A, B
					reg.write("A", reg.read("B"));
					cycles = 4;
				}
				case 0x79 -> { // LD A, C
					reg.write("A", reg.read("C"));
					cycles = 4;
				}
				case 0x7A -> { // LD A, D
					reg.write("A", reg.read("D"));
					cycles = 4;
				}
				case 0x7B -> { // LD A, E
					reg.write("A", reg.read("E"));
					cycles = 4;
				}
				case 0x7C -> { // LD A, H
					reg.write("A", reg.read("H"));
					cycles = 4;
				}
				case 0x7D -> { // LD A, L
					reg.write("A", reg.read("L"));
					cycles = 4;
				}
				case 0x7E -> { // LD A, (HL)
					reg.write("A", mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x7F -> { // LD A, A
					reg.write("A", reg.read("A"));
					cycles = 4;
				}
				case 0x80 -> { // ADD A, B
					ALU.instructADD(reg.read("B"));
					cycles = 4;
				}
				case 0x81 -> { // ADD A, C
					ALU.instructADD(reg.read("C"));
					cycles = 4;
				}
				case 0x82 -> { // ADD A, D
					ALU.instructADD(reg.read("D"));
					cycles = 4;
				}
				case 0x83 -> { // ADD A, E
					ALU.instructADD(reg.read("E"));
					cycles = 4;
				}
				case 0x84 -> { // ADD A, H
					ALU.instructADD(reg.read("H"));
					cycles = 4;
				}
				case 0x85 -> { // ADD A, L
					ALU.instructADD(reg.read("L"));
					cycles = 4;
				}
				case 0x86 -> { // ADD A, (HL)
					ALU.instructADD(mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x87 -> { // ADD A, A
					ALU.instructADD(reg.read("A"));
					cycles = 4;
				}
				case 0x88 -> { // ADC A, B
					ALU.instructADC(reg.read("B"));
					cycles = 4;
				}
				case 0x89 -> { // ADC A, C
					ALU.instructADC(reg.read("C"));
					cycles = 4;
				}
				case 0x8A -> { // ADC A, D
					ALU.instructADC(reg.read("D"));
					cycles = 4;
				}
				case 0x8B -> { // ADC A, E
					ALU.instructADC(reg.read("E"));
					cycles = 4;
				}
				case 0x8C -> { // ADC A, H
					ALU.instructADC(reg.read("H"));
					cycles = 4;
				}
				case 0x8D -> { // ADC A, L
					ALU.instructADC(reg.read("L"));
					cycles = 4;
				}
				case 0x8E -> { // ADC A, (HL)
					ALU.instructADC(mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x8F -> { // ADC A, A
					ALU.instructADC(reg.read("A"));
					cycles = 4;
				}
				case 0x90 -> { // SUB A, B
					ALU.instructSUB(reg.read("B"), false);
					cycles = 4;
				}
				case 0x91 -> { // SUB A, C
					ALU.instructSUB(reg.read("C"), false);
					cycles = 4;
				}
				case 0x92 -> { // SUB A, D
					ALU.instructSUB(reg.read("D"), false);
					cycles = 4;
				}
				case 0x93 -> { // SUB A, E
					ALU.instructSUB(reg.read("E"), false);
					cycles = 4;
				}
				case 0x94 -> { // SUB A, H
					ALU.instructSUB(reg.read("H"), false);
					cycles = 4;
				}
				case 0x95 -> { // SUB A, L
					ALU.instructSUB(reg.read("L"), false);
					cycles = 4;
				}
				case 0x96 -> { // SUB A, (HL)
					ALU.instructSUB(mmu.readByte(reg.read("HL")), false);
					cycles = 8;
				}
				case 0x97 -> { // SUB A, A
					ALU.instructSUB(reg.read("A"), false);
					cycles = 4;
				}
				case 0x98 -> { // SBC B
					ALU.instructSBC(reg.read("B"));
					cycles = 4;
				}
				case 0x99 -> { // SBC C
					ALU.instructSBC(reg.read("C"));
					cycles = 4;
				}
				case 0x9A -> { // SBC D
					ALU.instructSBC(reg.read("D"));
					cycles = 4;
				}
				case 0x9B -> { // SBC E
					ALU.instructSBC(reg.read("E"));
					cycles = 4;
				}
				case 0x9C -> { // SBC H
					ALU.instructSBC(reg.read("H"));
					cycles = 4;
				}
				case 0x9D -> { // SBC L
					ALU.instructSBC(reg.read("L"));
					cycles = 4;
				}
				case 0x9E -> { // SBC (HL)
					ALU.instructSBC(mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0x9F -> { // SBC A
					ALU.instructSBC(reg.read("A"));
					cycles = 4;
				}
				case 0xA0 -> { // AND B
					ALU.instructAND(reg.read("B"));
					cycles = 4;
				}
				case 0xA1 -> { // AND C
					ALU.instructAND(reg.read("C"));
					cycles = 4;
				}
				case 0xA2 -> { // AND D
					ALU.instructAND(reg.read("D"));
					cycles = 4;
				}
				case 0xA3 -> { // AND E
					ALU.instructAND(reg.read("E"));
					cycles = 4;
				}
				case 0xA4 -> { // AND H
					ALU.instructAND(reg.read("H"));
					cycles = 4;
				}
				case 0xA5 -> { // AND L
					ALU.instructAND(reg.read("L"));
					cycles = 4;
				}
				case 0xA6 -> { // AND (HL)
					ALU.instructAND(mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0xA7 -> { // AND A
					ALU.instructAND(reg.read("A"));
					cycles = 4;
				}
				case 0xA8 -> { // XOR A, B
					ALU.instructXOR(reg.read("B"));
					cycles = 4;
				}
				case 0xAB -> { // XOR A, E
					ALU.instructXOR(reg.read("E"));
					cycles = 4;
				}
				case 0xAC -> { // XOR A, H
					ALU.instructXOR(reg.read("H"));
					cycles = 4;
				}
				case 0xAD -> { // XOR A, L
					ALU.instructXOR(reg.read("L"));
					cycles = 4;
				}
				case 0xAE -> { // XOR A, (HL)
					ALU.instructXOR(mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0xA9 -> { // XOR A, C
					ALU.instructXOR(reg.read("C"));
					cycles = 4;
				}
				case 0xAA -> { // XOR D
					ALU.instructXOR(reg.read("D"));
					cycles = 4;
				}
				case 0xAF -> { // XOR A
					ALU.instructXOR(reg.read("A"));
					cycles = 4;
				}
				case 0xB0 -> { // OR A, B
					ALU.instructOR(reg.read("B"));
					cycles = 4;
				}
				case 0xB1 -> { // OR A, C
					ALU.instructOR(reg.read("C"));
					cycles = 4;
				}
				case 0xB2 -> { // OR A, D
					ALU.instructOR(reg.read("D"));
					cycles = 4;
				}
				case 0xB3 -> { // OR A, E
					ALU.instructOR(reg.read("E"));
					cycles = 4;
				}
				case 0xB4 -> { // OR A, H
					ALU.instructOR(reg.read("H"));
					cycles = 4;
				}
				case 0xB5 -> { // OR A, L
					ALU.instructOR(reg.read("L"));
					cycles = 4;
				}
				case 0xB6 -> { // OR A, (HL)
					ALU.instructOR(mmu.readByte(reg.read("HL")));
					cycles = 8;
				}
				case 0xB7 -> { // OR A, A
					ALU.instructOR(reg.read("A"));
					cycles = 4;
				}
				case 0xB8 -> { // CP A, B
					ALU.instructSUB(reg.read("B"), true);
					cycles = 4;
				}
				case 0xB9 -> { // CP A, C
					ALU.instructSUB(reg.read("C"), true);
					cycles = 4;
				}
				case 0xBA -> { // CP A, D
					ALU.instructSUB(reg.read("D"), true);
					cycles = 4;
				}
				case 0xBB -> { // CP A, E
					ALU.instructSUB(reg.read("E"), true);
					cycles = 4;
				}
				case 0xBC -> { // CP A, H
					ALU.instructSUB(reg.read("H"), true);
					cycles = 4;
				}
				case 0xBD -> { // CP A, L
					ALU.instructSUB(reg.read("L"), true);
					cycles = 4;
				}
				case 0xBE -> { // CP A, (HL)
					ALU.instructSUB(mmu.readByte(reg.read("HL")), true);
					cycles = 8;
				}
				case 0xBF -> { // CP A, A
					ALU.instructSUB(reg.read("A"), true);
					cycles = 4;
				}
				case 0xC1 -> { // POP BC
					ControlFlow.instructPOP("BC");
					cycles = 12;
				}
				case 0xC3 -> { // JP u16
					ControlFlow.instructJP(fetchNextWord());
					cycles = 16;
				} // CALL Z, u16
					// CALL NZ, u16
					// CALL NC, u16
				case 0xCC, 0xC4, 0xD4, 0xDC -> // CALL C, u16
					cycles = ControlFlow.instructCondCALL(instruction, fetchNextWord());
				case 0xC5 -> { // PUSH BC
					ControlFlow.instructPUSH("BC");
					cycles = 16;
				}
				case 0xC6 -> { // ADD A, u8
					ALU.instructADD(fetchNextByte());
					cycles = 8;
				} // RST 00
					// RST 08
					// RST 10
					// RST 18
					// RST 20
					// RST 28
					// RST 30
				case 0xC7, 0xCF, 0xD7, 0xDF, 0xE7, 0xEF, 0xF7, 0xFF -> {// RST 38
					ControlFlow.instructRST(instruction);
					cycles = 16;
				}
				case 0xC9 -> { // RET
					ControlFlow.instructRET();
					cycles = 16;
				} // JP NZ, u16
					// JP Z, u16
					// JP NC, u16
				case 0xC2, 0xCA, 0xD2, 0xDA -> // JP C, u16
					cycles = ControlFlow.instructCondJP(instruction, fetchNextWord());
				case 0xCB -> // send to CB handling function
					cycles = nextCBInstruction();
				case 0xCD -> { // CALL u16
					ControlFlow.instructCALL(fetchNextWord());
					cycles = 24;
				}
				case 0xCE -> { // ADC A, u8
					ALU.instructADC(fetchNextByte());
					cycles = 8;
				} // RET Z
					// RET NZ
					// RET C
				case 0xC8, 0xC0, 0xD8, 0xD0 -> // RET NC
					cycles = ControlFlow.instructCondRET(instruction);
				case 0xD1 -> { // POP DE
					ControlFlow.instructPOP("DE");
					cycles = 12;
				}
				case 0xD5 -> { // PUSH DE
					ControlFlow.instructPUSH("DE");
					cycles = 16;
				}
				case 0xD6 -> { // SUB A, u8
					ALU.instructSUB(fetchNextByte(), false);
					cycles = 8;
				}
				case 0xD9 -> { // RETI
					ControlFlow.instructRETI();
					cycles = 16;
				}
				case 0xDE -> { // SBC A, u8
					ALU.instructSBC(fetchNextByte());
					cycles = 8;
				}
				case 0xE0 -> { // LD ($FF00+n), A
					mmu.writeByte(0xFF00 + fetchNextByte(), reg.read("A"));
					cycles = 12;
				}
				case 0xE1 -> { // POP HL
					ControlFlow.instructPOP("HL");
					cycles = 12;
				}
				case 0xE2 -> { // LD ($FF00+C), A
					mmu.writeByte(0xFF00 + reg.read("C"), reg.read("A"));
					cycles = 8;
				}
				case 0xE5 -> { // PUSH HL
					ControlFlow.instructPUSH("HL");
					cycles = 16;
				}
				case 0xE6 -> { // AND A, u8
					ALU.instructAND(fetchNextByte());
					cycles = 8;
				}
				case 0xE8 -> { // ADD SP, s8
					ALU.instructADDSP((byte) fetchNextByte());
					cycles = 16;
				}
				case 0xE9 -> { // JP HL
					ControlFlow.instructJP(reg.read("HL"));
					cycles = 4;
				}
				case 0xEA -> { // LD (u16), A
					mmu.writeByte(fetchNextWord(), reg.read("A"));
					cycles = 16;
				}
				case 0xEE -> { // XOR A, u8
					ALU.instructXOR(fetchNextByte());
					cycles = 8;
				}
				case 0xF0 -> { // LD A, ($FF00+n)
					reg.write("A", mmu.readByte(0xFF00 + fetchNextByte()));
					cycles = 12;
				}
				case 0xF1 -> { // POP AF
					ControlFlow.instructPOP("AF");
					cycles = 12;
				}
				case 0xF2 -> { // LD A, (FF00+C)
					reg.write("A", mmu.readByte(reg.read("C") + 0xFF00));
					cycles = 8;
				}
				case 0xF3 -> { // DI
					ControlFlow.instructDI();
					cycles = 4;
				}
				case 0xF5 -> { // PUSH AF
					ControlFlow.instructPUSH("AF");
					cycles = 16;
				}
				case 0xF6 -> { // OR A, u8
					ALU.instructOR(fetchNextByte());
					cycles = 8;
				}
				case 0xF8 -> { // LD HL, SP+s8
					byte value = (byte) fetchNextByte();
					int sp = reg.read("SP");
					int result = (sp + value) & 0xFFFF;
					reg.write("HL", result);
					reg.getFR().setZ(false);
					reg.getFR().setN(false);
					reg.getFR().setC(((sp ^ value ^ result) & 0x100) == 0x100);
					reg.getFR().setH(((sp ^ value ^ result) & 0x10) == 0x10);
					cycles = 12;
				}
				case 0xF9 -> { // LD SP, HL
					reg.write("SP", reg.read("HL"));
					cycles = 8;
				}
				case 0xFA -> { // LD A, (u16)
					reg.write("A", mmu.readByte(fetchNextWord()));
					cycles = 16;
				}
				case 0xFB -> { // EI
					delayedEI = true;
					cycles = 4;
				}
				case 0xFE -> { // CP A, u8
					ALU.instructSUB(fetchNextByte(), true);
					cycles = 8;
				}
				default ->
					throw new IllegalArgumentException(String.format("Unhandled CPU instruction 0x%X", instruction));
			}
			cpuCycles += cycles;
			return cycles;
		} else {
			// The system clock still runs while halted, 4 cycles for that.
			cpuCycles += 4;
			return 4;
		}
	}

	/**
	 * Fetches the next byte in memory and executes the associated CB instruction.
	 * 
	 * @return The number of cycles for the executed CB instruction.
	 */
	@SuppressWarnings("java:S1479")
	private int nextCBInstruction() {
		int instruction = fetchNextByte();
		int cycles;
		switch (instruction) {
			case 0x00 -> { // RLC B
				BitShift.instructRLC("B");
				cycles = 8;
			}
			case 0x01 -> { // RLC C
				BitShift.instructRLC("C");
				cycles = 8;
			}
			case 0x02 -> { // RLC D
				BitShift.instructRLC("D");
				cycles = 8;
			}
			case 0x03 -> { // RLC E
				BitShift.instructRLC("E");
				cycles = 8;
			}
			case 0x04 -> { // RLC H
				BitShift.instructRLC("H");
				cycles = 8;
			}
			case 0x05 -> { // RLC L
				BitShift.instructRLC("L");
				cycles = 8;
			}
			case 0x06 -> { // RLC (HL)
				BitShift.instructRLC("HL");
				cycles = 16;
			}
			case 0x07 -> { // RLC A
				BitShift.instructRLC("A");
				cycles = 8;
			}
			case 0x08 -> { // RRC B
				BitShift.instructRRC("B");
				cycles = 8;
			}
			case 0x09 -> { // RRC C
				BitShift.instructRRC("C");
				cycles = 8;
			}
			case 0x0A -> { // RRC D
				BitShift.instructRRC("D");
				cycles = 8;
			}
			case 0x0B -> { // RRC E
				BitShift.instructRRC("E");
				cycles = 8;
			}
			case 0x0C -> { // RRC H
				BitShift.instructRRC("H");
				cycles = 8;
			}
			case 0x0D -> { // RRC L
				BitShift.instructRRC("L");
				cycles = 8;
			}
			case 0x0E -> { // RRC (HL)
				BitShift.instructRRC("HL");
				cycles = 16;
			}
			case 0x0F -> { // RRC A
				BitShift.instructRRC("A");
				cycles = 8;
			}
			case 0x10 -> { // RL B
				BitShift.instructRL("B");
				cycles = 8;
			}
			case 0x11 -> { // RL C
				BitShift.instructRL("C");
				cycles = 8;
			}
			case 0x12 -> { // RL D
				BitShift.instructRL("D");
				cycles = 8;
			}
			case 0x13 -> { // RL E
				BitShift.instructRL("E");
				cycles = 8;
			}
			case 0x14 -> { // RL H
				BitShift.instructRL("H");
				cycles = 8;
			}
			case 0x15 -> { // RL L
				BitShift.instructRL("L");
				cycles = 8;
			}
			case 0x16 -> { // RL (HL)
				BitShift.instructRL("HL");
				cycles = 16;
			}
			case 0x17 -> { // RL A
				BitShift.instructRL("A");
				cycles = 8;
			}
			case 0x18 -> { // RR B
				BitShift.instructRR("B");
				cycles = 8;
			}
			case 0x19 -> { // RR C
				BitShift.instructRR("C");
				cycles = 8;
			}
			case 0x1A -> { // RR D
				BitShift.instructRR("D");
				cycles = 8;
			}
			case 0x1B -> { // RR E
				BitShift.instructRR("E");
				cycles = 8;
			}
			case 0x1C -> { // RR H
				BitShift.instructRR("H");
				cycles = 8;
			}
			case 0x1D -> { // RR L
				BitShift.instructRR("L");
				cycles = 8;
			}
			case 0x1E -> { // RR (HL)
				BitShift.instructRR("HL");
				cycles = 16;
			}
			case 0x1F -> { // RR A
				BitShift.instructRR("A");
				cycles = 8;
			}
			case 0x20 -> { // SLA B
				BitShift.instructSLA("B");
				cycles = 8;
			}
			case 0x21 -> { // SLA C
				BitShift.instructSLA("C");
				cycles = 8;
			}
			case 0x22 -> { // SLA D
				BitShift.instructSLA("D");
				cycles = 8;
			}
			case 0x23 -> { // SLA E
				BitShift.instructSLA("E");
				cycles = 8;
			}
			case 0x24 -> { // SLA H
				BitShift.instructSLA("H");
				cycles = 8;
			}
			case 0x25 -> { // SLA L
				BitShift.instructSLA("L");
				cycles = 8;
			}
			case 0x26 -> { // SLA (HL)
				BitShift.instructSLA("HL");
				cycles = 16;
			}
			case 0x27 -> { // SLA A
				BitShift.instructSLA("A");
				cycles = 8;
			}
			case 0x28 -> { // SRA B
				BitShift.instructSRA("B");
				cycles = 8;
			}
			case 0x29 -> { // SRA C
				BitShift.instructSRA("C");
				cycles = 8;
			}
			case 0x2A -> { // SRA D
				BitShift.instructSRA("D");
				cycles = 8;
			}
			case 0x2B -> { // SRA E
				BitShift.instructSRA("E");
				cycles = 8;
			}
			case 0x2C -> { // SRA H
				BitShift.instructSRA("H");
				cycles = 8;
			}
			case 0x2D -> { // SRA L
				BitShift.instructSRA("L");
				cycles = 8;
			}
			case 0x2E -> { // SRA (HL)
				BitShift.instructSRA("HL");
				cycles = 16;
			}
			case 0x2F -> { // SRA A
				BitShift.instructSRA("A");
				cycles = 8;
			}
			case 0x30 -> { // SWAP B
				BitShift.instructSWAP("B");
				cycles = 8;
			}
			case 0x31 -> { // SWAP C
				BitShift.instructSWAP("C");
				cycles = 8;
			}
			case 0x32 -> { // SWAP D
				BitShift.instructSWAP("D");
				cycles = 8;
			}
			case 0x33 -> { // SWAP E
				BitShift.instructSWAP("E");
				cycles = 8;
			}
			case 0x34 -> { // SWAP H
				BitShift.instructSWAP("H");
				cycles = 8;
			}
			case 0x35 -> { // SWAP L
				BitShift.instructSWAP("L");
				cycles = 8;
			}
			case 0x36 -> { // SWAP (HL)
				BitShift.instructSWAP("HL");
				cycles = 16;
			}
			case 0x37 -> { // SWAP A
				BitShift.instructSWAP("A");
				cycles = 8;
			}
			case 0x38 -> { // SRL B
				BitShift.instructSRL("B");
				cycles = 8;
			}
			case 0x39 -> { // SRL C
				BitShift.instructSRL("C");
				cycles = 8;
			}
			case 0x3A -> { // SRL D
				BitShift.instructSRL("D");
				cycles = 8;
			}
			case 0x3B -> { // SRL E
				BitShift.instructSRL("E");
				cycles = 8;
			}
			case 0x3C -> { // SRL H
				BitShift.instructSRL("H");
				cycles = 8;
			}
			case 0x3D -> { // SRL L
				BitShift.instructSRL("L");
				cycles = 8;
			}
			case 0x3E -> { // SRL (HL)
				BitShift.instructSRL("HL");
				cycles = 16;
			}
			case 0x3F -> { // SRL A
				BitShift.instructSRL("A");
				cycles = 8;
			}
			case 0x40 -> { // BIT 0, B
				BitShift.instructBIT("B", 0);
				cycles = 8;
			}
			case 0x41 -> { // BIT 0, C
				BitShift.instructBIT("C", 0);
				cycles = 8;
			}
			case 0x42 -> { // BIT 0, D
				BitShift.instructBIT("D", 0);
				cycles = 8;
			}
			case 0x43 -> { // BIT 0, E
				BitShift.instructBIT("E", 0);
				cycles = 8;
			}
			case 0x44 -> { // BIT 0, H
				BitShift.instructBIT("H", 0);
				cycles = 8;
			}
			case 0x45 -> { // BIT 0, L
				BitShift.instructBIT("L", 0);
				cycles = 8;
			}
			case 0x46 -> { // BIT 0, (HL)
				BitShift.instructBIT("HL", 0);
				cycles = 12;
			}
			case 0x47 -> { // BIT 0, A
				BitShift.instructBIT("A", 0);
				cycles = 8;
			}
			case 0x48 -> { // BIT 1, B
				BitShift.instructBIT("B", 1);
				cycles = 8;
			}
			case 0x49 -> { // BIT 1, C
				BitShift.instructBIT("C", 1);
				cycles = 8;
			}
			case 0x4A -> { // BIT 1, D
				BitShift.instructBIT("D", 1);
				cycles = 8;
			}
			case 0x4B -> { // BIT 1, E
				BitShift.instructBIT("E", 1);
				cycles = 8;
			}
			case 0x4C -> { // BIT 1, H
				BitShift.instructBIT("H", 1);
				cycles = 8;
			}
			case 0x4D -> { // BIT 1, L
				BitShift.instructBIT("L", 1);
				cycles = 8;
			}
			case 0x4E -> { // BIT 1, (HL)
				BitShift.instructBIT("HL", 1);
				cycles = 12;
			}
			case 0x4F -> { // BIT 1, A
				BitShift.instructBIT("A", 1);
				cycles = 8;
			}
			case 0x50 -> { // BIT 2, B
				BitShift.instructBIT("B", 2);
				cycles = 8;
			}
			case 0x51 -> { // BIT 2, C
				BitShift.instructBIT("C", 2);
				cycles = 8;
			}
			case 0x52 -> { // BIT 2, D
				BitShift.instructBIT("D", 2);
				cycles = 8;
			}
			case 0x53 -> { // BIT 2, E
				BitShift.instructBIT("E", 2);
				cycles = 8;
			}
			case 0x54 -> { // BIT 2, H
				BitShift.instructBIT("H", 2);
				cycles = 8;
			}
			case 0x55 -> { // BIT 2, L
				BitShift.instructBIT("L", 2);
				cycles = 8;
			}
			case 0x56 -> { // BIT 2, (HL)
				BitShift.instructBIT("HL", 2);
				cycles = 12;
			}
			case 0x57 -> { // BIT 2, A
				BitShift.instructBIT("A", 2);
				cycles = 8;
			}
			case 0x58 -> { // BIT 3, B
				BitShift.instructBIT("B", 3);
				cycles = 8;
			}
			case 0x59 -> { // BIT 3, C
				BitShift.instructBIT("C", 3);
				cycles = 8;
			}
			case 0x5A -> { // BIT 3, D
				BitShift.instructBIT("D", 3);
				cycles = 8;
			}
			case 0x5B -> { // BIT 3, E
				BitShift.instructBIT("E", 3);
				cycles = 8;
			}
			case 0x5C -> { // BIT 3, H
				BitShift.instructBIT("H", 3);
				cycles = 8;
			}
			case 0x5D -> { // BIT 3, L
				BitShift.instructBIT("L", 3);
				cycles = 8;
			}
			case 0x5E -> { // BIT 3, (HL)
				BitShift.instructBIT("HL", 3);
				cycles = 12;
			}
			case 0x5F -> { // BIT 3, A
				BitShift.instructBIT("A", 3);
				cycles = 8;
			}
			case 0x60 -> { // BIT 4, B
				BitShift.instructBIT("B", 4);
				cycles = 8;
			}
			case 0x61 -> { // BIT 4, C
				BitShift.instructBIT("C", 4);
				cycles = 8;
			}
			case 0x62 -> { // BIT 4, D
				BitShift.instructBIT("D", 4);
				cycles = 8;
			}
			case 0x63 -> { // BIT 4, E
				BitShift.instructBIT("E", 4);
				cycles = 8;
			}
			case 0x64 -> { // BIT 4, H
				BitShift.instructBIT("H", 4);
				cycles = 8;
			}
			case 0x65 -> { // BIT 4, L
				BitShift.instructBIT("L", 4);
				cycles = 8;
			}
			case 0x66 -> { // BIT 4, (HL)
				BitShift.instructBIT("HL", 4);
				cycles = 12;
			}
			case 0x67 -> { // BIT 4, A
				BitShift.instructBIT("A", 4);
				cycles = 8;
			}
			case 0x68 -> { // BIT 5, B
				BitShift.instructBIT("B", 5);
				cycles = 8;
			}
			case 0x69 -> { // BIT 5, C
				BitShift.instructBIT("C", 5);
				cycles = 8;
			}
			case 0x6A -> { // BIT 5, D
				BitShift.instructBIT("D", 5);
				cycles = 8;
			}
			case 0x6B -> { // BIT 5, E
				BitShift.instructBIT("E", 5);
				cycles = 8;
			}
			case 0x6C -> { // BIT 5, H
				BitShift.instructBIT("H", 5);
				cycles = 8;
			}
			case 0x6D -> { // BIT 5, L
				BitShift.instructBIT("L", 5);
				cycles = 8;
			}
			case 0x6E -> { // BIT 5, (HL)
				BitShift.instructBIT("HL", 5);
				cycles = 12;
			}
			case 0x6F -> { // BIT 5, A
				BitShift.instructBIT("A", 5);
				cycles = 8;
			}
			case 0x70 -> { // BIT 6, B
				BitShift.instructBIT("B", 6);
				cycles = 8;
			}
			case 0x71 -> { // BIT 6, C
				BitShift.instructBIT("C", 6);
				cycles = 8;
			}
			case 0x72 -> { // BIT 6, D
				BitShift.instructBIT("D", 6);
				cycles = 8;
			}
			case 0x73 -> { // BIT 6, E
				BitShift.instructBIT("E", 6);
				cycles = 8;
			}
			case 0x74 -> { // BIT 6, H
				BitShift.instructBIT("H", 6);
				cycles = 8;
			}
			case 0x75 -> { // BIT 6, L
				BitShift.instructBIT("L", 6);
				cycles = 8;
			}
			case 0x76 -> { // BIT 6, (HL)
				BitShift.instructBIT("HL", 6);
				cycles = 12;
			}
			case 0x77 -> { // BIT 6, A
				BitShift.instructBIT("A", 6);
				cycles = 8;
			}
			case 0x78 -> { // BIT 7, B
				BitShift.instructBIT("B", 7);
				cycles = 8;
			}
			case 0x79 -> { // BIT 7, C
				BitShift.instructBIT("C", 7);
				cycles = 8;
			}
			case 0x7A -> { // BIT 7, D
				BitShift.instructBIT("D", 7);
				cycles = 8;
			}
			case 0x7B -> { // BIT 7, E
				BitShift.instructBIT("E", 7);
				cycles = 8;
			}
			case 0x7C -> { // BIT 7, H
				BitShift.instructBIT("H", 7);
				cycles = 8;
			}
			case 0x7D -> { // BIT 7, L
				BitShift.instructBIT("L", 7);
				cycles = 8;
			}
			case 0x7E -> { // BIT 7, (HL)
				BitShift.instructBIT("HL", 7);
				cycles = 12;
			}
			case 0x7F -> { // BIT 7, A
				BitShift.instructBIT("A", 7);
				cycles = 8;
			}
			case 0x80 -> { // RES 0, B
				BitShift.instructRES("B", 0);
				cycles = 8;
			}
			case 0x81 -> { // RES 0, C
				BitShift.instructRES("C", 0);
				cycles = 8;
			}
			case 0x82 -> { // RES 0, D
				BitShift.instructRES("D", 0);
				cycles = 8;
			}
			case 0x83 -> { // RES 0, E
				BitShift.instructRES("E", 0);
				cycles = 8;
			}
			case 0x84 -> { // RES 0, H
				BitShift.instructRES("H", 0);
				cycles = 8;
			}
			case 0x85 -> { // RES 0, L
				BitShift.instructRES("L", 0);
				cycles = 8;
			}
			case 0x86 -> { // RES 0, (HL)
				BitShift.instructRES("HL", 0);
				cycles = 16;
			}
			case 0x87 -> { // RES 0, A
				BitShift.instructRES("A", 0);
				cycles = 8;
			}
			case 0x88 -> { // RES 1, B
				BitShift.instructRES("B", 1);
				cycles = 8;
			}
			case 0x89 -> { // RES 1, C
				BitShift.instructRES("C", 1);
				cycles = 8;
			}
			case 0x8A -> { // RES 1, D
				BitShift.instructRES("D", 1);
				cycles = 8;
			}
			case 0x8B -> { // RES 1, E
				BitShift.instructRES("E", 1);
				cycles = 8;
			}
			case 0x8C -> { // RES 1, H
				BitShift.instructRES("H", 1);
				cycles = 8;
			}
			case 0x8D -> { // RES 1, L
				BitShift.instructRES("L", 1);
				cycles = 8;
			}
			case 0x8E -> { // RES 1, (HL)
				BitShift.instructRES("HL", 1);
				cycles = 16;
			}
			case 0x8F -> { // RES 1, A
				BitShift.instructRES("A", 1);
				cycles = 8;
			}
			case 0x90 -> { // RES 2, B
				BitShift.instructRES("B", 2);
				cycles = 8;
			}
			case 0x91 -> { // RES 2, C
				BitShift.instructRES("C", 2);
				cycles = 8;
			}
			case 0x92 -> { // RES 2, D
				BitShift.instructRES("D", 2);
				cycles = 8;
			}
			case 0x93 -> { // RES 2, E
				BitShift.instructRES("E", 2);
				cycles = 8;
			}
			case 0x94 -> { // RES 2, H
				BitShift.instructRES("H", 2);
				cycles = 8;
			}
			case 0x95 -> { // RES 2, L
				BitShift.instructRES("L", 2);
				cycles = 8;
			}
			case 0x96 -> { // RES 2, (HL)
				BitShift.instructRES("HL", 2);
				cycles = 16;
			}
			case 0x97 -> { // RES 2, A
				BitShift.instructRES("A", 2);
				cycles = 8;
			}
			case 0x98 -> { // RES 3, B
				BitShift.instructRES("B", 3);
				cycles = 8;
			}
			case 0x99 -> { // RES 3, C
				BitShift.instructRES("C", 3);
				cycles = 8;
			}
			case 0x9A -> { // RES 3, D
				BitShift.instructRES("D", 3);
				cycles = 8;
			}
			case 0x9B -> { // RES 3, E
				BitShift.instructRES("E", 3);
				cycles = 8;
			}
			case 0x9C -> { // RES 3, H
				BitShift.instructRES("H", 3);
				cycles = 8;
			}
			case 0x9D -> { // RES 3, L
				BitShift.instructRES("L", 3);
				cycles = 8;
			}
			case 0x9E -> { // RES 3, (HL)
				BitShift.instructRES("HL", 3);
				cycles = 16;
			}
			case 0x9F -> { // RES 3, A
				BitShift.instructRES("A", 3);
				cycles = 8;
			}
			case 0xA0 -> { // RES 4, B
				BitShift.instructRES("B", 4);
				cycles = 8;
			}
			case 0xA1 -> { // RES 4, C
				BitShift.instructRES("C", 4);
				cycles = 8;
			}
			case 0xA2 -> { // RES 4, D
				BitShift.instructRES("D", 4);
				cycles = 8;
			}
			case 0xA3 -> { // RES 4, E
				BitShift.instructRES("E", 4);
				cycles = 8;
			}
			case 0xA4 -> { // RES 4, H
				BitShift.instructRES("H", 4);
				cycles = 8;
			}
			case 0xA5 -> { // RES 4, L
				BitShift.instructRES("L", 4);
				cycles = 8;
			}
			case 0xA6 -> { // RES 4, (HL)
				BitShift.instructRES("HL", 4);
				cycles = 16;
			}
			case 0xA7 -> { // RES 4, A
				BitShift.instructRES("A", 4);
				cycles = 8;
			}
			case 0xA8 -> { // RES 5, B
				BitShift.instructRES("B", 5);
				cycles = 8;
			}
			case 0xA9 -> { // RES 5, C
				BitShift.instructRES("C", 5);
				cycles = 8;
			}
			case 0xAA -> { // RES 5, D
				BitShift.instructRES("D", 5);
				cycles = 8;
			}
			case 0xAB -> { // RES 5, E
				BitShift.instructRES("E", 5);
				cycles = 8;
			}
			case 0xAC -> { // RES 5, H
				BitShift.instructRES("H", 5);
				cycles = 8;
			}
			case 0xAD -> { // RES 5, L
				BitShift.instructRES("L", 5);
				cycles = 8;
			}
			case 0xAE -> { // RES 5, (HL)
				BitShift.instructRES("HL", 5);
				cycles = 16;
			}
			case 0xAF -> { // RES 5, A
				BitShift.instructRES("A", 5);
				cycles = 8;
			}
			case 0xB0 -> { // RES 6, B
				BitShift.instructRES("B", 6);
				cycles = 8;
			}
			case 0xB1 -> { // RES 6, C
				BitShift.instructRES("C", 6);
				cycles = 8;
			}
			case 0xB2 -> { // RES 6, D
				BitShift.instructRES("D", 6);
				cycles = 8;
			}
			case 0xB3 -> { // RES 6, E
				BitShift.instructRES("E", 6);
				cycles = 8;
			}
			case 0xB4 -> { // RES 6, H
				BitShift.instructRES("H", 6);
				cycles = 8;
			}
			case 0xB5 -> { // RES 6, L
				BitShift.instructRES("L", 6);
				cycles = 8;
			}
			case 0xB6 -> { // RES 6, (HL)
				BitShift.instructRES("HL", 6);
				cycles = 16;
			}
			case 0xB7 -> { // RES 6, A
				BitShift.instructRES("A", 6);
				cycles = 8;
			}
			case 0xB8 -> { // RES 7, B
				BitShift.instructRES("B", 7);
				cycles = 8;
			}
			case 0xB9 -> { // RES 7, C
				BitShift.instructRES("C", 7);
				cycles = 8;
			}
			case 0xBA -> { // RES 7, D
				BitShift.instructRES("D", 7);
				cycles = 8;
			}
			case 0xBB -> { // RES 7, E
				BitShift.instructRES("E", 7);
				cycles = 8;
			}
			case 0xBC -> { // RES 7, H
				BitShift.instructRES("H", 7);
				cycles = 8;
			}
			case 0xBD -> { // RES 7, L
				BitShift.instructRES("L", 7);
				cycles = 8;
			}
			case 0xBE -> { // RES 7, (HL)
				BitShift.instructRES("HL", 7);
				cycles = 16;
			}
			case 0xBF -> { // RES 7, A
				BitShift.instructRES("A", 7);
				cycles = 8;
			}
			case 0xC0 -> { // SET 0, B
				BitShift.instructSET("B", 0);
				cycles = 8;
			}
			case 0xC1 -> { // SET 0, C
				BitShift.instructSET("C", 0);
				cycles = 8;
			}
			case 0xC2 -> { // SET 0, D
				BitShift.instructSET("D", 0);
				cycles = 8;
			}
			case 0xC3 -> { // SET 0, E
				BitShift.instructSET("E", 0);
				cycles = 8;
			}
			case 0xC4 -> { // SET 0, H
				BitShift.instructSET("H", 0);
				cycles = 8;
			}
			case 0xC5 -> { // SET 0, L
				BitShift.instructSET("L", 0);
				cycles = 8;
			}
			case 0xC6 -> { // SET 0, (HL)
				BitShift.instructSET("HL", 0);
				cycles = 16;
			}
			case 0xC7 -> { // SET 0, A
				BitShift.instructSET("A", 0);
				cycles = 8;
			}
			case 0xC8 -> { // SET 1, B
				BitShift.instructSET("B", 1);
				cycles = 8;
			}
			case 0xC9 -> { // SET 1, C
				BitShift.instructSET("C", 1);
				cycles = 8;
			}
			case 0xCA -> { // SET 1, D
				BitShift.instructSET("D", 1);
				cycles = 8;
			}
			case 0xCB -> { // SET 1, E
				BitShift.instructSET("E", 1);
				cycles = 8;
			}
			case 0xCC -> { // SET 1, H
				BitShift.instructSET("H", 1);
				cycles = 8;
			}
			case 0xCD -> { // SET 1, L
				BitShift.instructSET("L", 1);
				cycles = 8;
			}
			case 0xCE -> { // SET 1, (HL)
				BitShift.instructSET("HL", 1);
				cycles = 16;
			}
			case 0xCF -> { // SET 1, A
				BitShift.instructSET("A", 1);
				cycles = 8;
			}
			case 0xD0 -> { // SET 2, B
				BitShift.instructSET("B", 2);
				cycles = 8;
			}
			case 0xD1 -> { // SET 2, C
				BitShift.instructSET("C", 2);
				cycles = 8;
			}
			case 0xD2 -> { // SET 2, D
				BitShift.instructSET("D", 2);
				cycles = 8;
			}
			case 0xD3 -> { // SET 2, E
				BitShift.instructSET("E", 2);
				cycles = 8;
			}
			case 0xD4 -> { // SET 2, H
				BitShift.instructSET("H", 2);
				cycles = 8;
			}
			case 0xD5 -> { // SET 2, L
				BitShift.instructSET("L", 2);
				cycles = 8;
			}
			case 0xD6 -> { // SET 2, (HL)
				BitShift.instructSET("HL", 2);
				cycles = 16;
			}
			case 0xD7 -> { // SET 2, A
				BitShift.instructSET("A", 2);
				cycles = 8;
			}
			case 0xD8 -> { // SET 3, B
				BitShift.instructSET("B", 3);
				cycles = 8;
			}
			case 0xD9 -> { // SET 3, C
				BitShift.instructSET("C", 3);
				cycles = 8;
			}
			case 0xDA -> { // SET 3, D
				BitShift.instructSET("D", 3);
				cycles = 8;
			}
			case 0xDB -> { // SET 3, E
				BitShift.instructSET("E", 3);
				cycles = 8;
			}
			case 0xDC -> { // SET 3, H
				BitShift.instructSET("H", 3);
				cycles = 8;
			}
			case 0xDD -> { // SET 3, L
				BitShift.instructSET("L", 3);
				cycles = 8;
			}
			case 0xDE -> { // SET 3, (HL)
				BitShift.instructSET("HL", 3);
				cycles = 16;
			}
			case 0xDF -> { // SET 3, A
				BitShift.instructSET("A", 3);
				cycles = 8;
			}
			case 0xE0 -> { // SET 4, B
				BitShift.instructSET("B", 4);
				cycles = 8;
			}
			case 0xE1 -> { // SET 4, C
				BitShift.instructSET("C", 4);
				cycles = 8;
			}
			case 0xE2 -> { // SET 4, D
				BitShift.instructSET("D", 4);
				cycles = 8;
			}
			case 0xE3 -> { // SET 4, E
				BitShift.instructSET("E", 4);
				cycles = 8;
			}
			case 0xE4 -> { // SET 4, H
				BitShift.instructSET("H", 4);
				cycles = 8;
			}
			case 0xE5 -> { // SET 4, L
				BitShift.instructSET("L", 4);
				cycles = 8;
			}
			case 0xE6 -> { // SET 4, (HL)
				BitShift.instructSET("HL", 4);
				cycles = 16;
			}
			case 0xE7 -> { // SET 4, A
				BitShift.instructSET("A", 4);
				cycles = 8;
			}
			case 0xE8 -> { // SET 5, B
				BitShift.instructSET("B", 5);
				cycles = 8;
			}
			case 0xE9 -> { // SET 5, C
				BitShift.instructSET("C", 5);
				cycles = 8;
			}
			case 0xEA -> { // SET 5, D
				BitShift.instructSET("D", 5);
				cycles = 8;
			}
			case 0xEB -> { // SET 5, E
				BitShift.instructSET("E", 5);
				cycles = 8;
			}
			case 0xEC -> { // SET 5, H
				BitShift.instructSET("H", 5);
				cycles = 8;
			}
			case 0xED -> { // SET 5, L
				BitShift.instructSET("L", 5);
				cycles = 8;
			}
			case 0xEE -> { // SET 5, (HL)
				BitShift.instructSET("HL", 5);
				cycles = 16;
			}
			case 0xEF -> { // SET 5, A
				BitShift.instructSET("A", 5);
				cycles = 8;
			}
			case 0xF0 -> { // SET 6, B
				BitShift.instructSET("B", 6);
				cycles = 8;
			}
			case 0xF1 -> { // SET 6, C
				BitShift.instructSET("C", 6);
				cycles = 8;
			}
			case 0xF2 -> { // SET 6, D
				BitShift.instructSET("D", 6);
				cycles = 8;
			}
			case 0xF3 -> { // SET 6, E
				BitShift.instructSET("E", 6);
				cycles = 8;
			}
			case 0xF4 -> { // SET 6, H
				BitShift.instructSET("H", 6);
				cycles = 8;
			}
			case 0xF5 -> { // SET 6, L
				BitShift.instructSET("L", 6);
				cycles = 8;
			}
			case 0xF6 -> { // SET 6, (HL)
				BitShift.instructSET("HL", 6);
				cycles = 16;
			}
			case 0xF7 -> { // SET 6, A
				BitShift.instructSET("A", 6);
				cycles = 8;
			}
			case 0xF8 -> { // SET 7, B
				BitShift.instructSET("B", 7);
				cycles = 8;
			}
			case 0xF9 -> { // SET 7, C
				BitShift.instructSET("C", 7);
				cycles = 8;
			}
			case 0xFA -> { // SET 7, D
				BitShift.instructSET("D", 7);
				cycles = 8;
			}
			case 0xFB -> { // SET 7, E
				BitShift.instructSET("E", 7);
				cycles = 8;
			}
			case 0xFC -> { // SET 7, H
				BitShift.instructSET("H", 7);
				cycles = 8;
			}
			case 0xFD -> { // SET 7, L
				BitShift.instructSET("L", 7);
				cycles = 8;
			}
			case 0xFE -> { // SET 7, (HL)
				BitShift.instructSET("HL", 7);
				cycles = 16;
			}
			case 0xFF -> { // SET 7, A
				BitShift.instructSET("A", 7);
				cycles = 8;
			}
			default -> throw new IllegalArgumentException(String.format("Unhandled CB instruction 0x%X", instruction));
		}
		return cycles;
	}

	/**
	 * Fetches the next byte and increments PC by 1.
	 * 
	 * @return The next byte in memory.
	 */
	public int fetchNextByte() {
		int result = mmu.readByte(reg.getPC());
		reg.incPC();
		return result;
	}

	/**
	 * Fetches the next 2 bytes and increments PC by 2.
	 * 
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

	public boolean isHalted() {
		return isHalted;
	}

	public void setHalted(boolean value) {
		this.isHalted = value;
	}
}
