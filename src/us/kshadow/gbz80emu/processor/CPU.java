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

	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	private static final Logger logger = Logger.getLogger("GBZ80Emu");
	private int cpuCycles;
	private boolean isRunning;
	
	public CPU() {
		cpuCycles = 0;
		isRunning = true;
		mmu.toggleBootROM(true);
		logger.log(Level.INFO, "CPU execution started.");
	}
	
	public int nextInstruction() {
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
			ALU.instructDECu16("E");
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
		/*case 0x27: // DAA
			TODO: handle DAA here
			cycles = 4;
			break;*/
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
		case 0x35: // DEC (HL)
			ALU.instructDECu8("HL");
			cycles = 12;
			break;
		case 0x39: // ADD HL, SP
			ALU.instructADDu16("SP");
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
		case 0x46: // LD B, (HL)
			reg.write("B", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x47: // LD B, A
			reg.write("B", reg.read("A"));
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
		case 0x56: // LD D, (HL)
			reg.write("D", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x57: // LD D, A
			reg.write("D", reg.read("A"));
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
		case 0x62: // LD H, D
			reg.write("H", reg.read("D"));
			cycles = 4;
			break;
		case 0x66: // LD B, u8
			reg.write("H", mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x67: // LD H, A
			reg.write("H", reg.read("A"));
			cycles = 4;
			break;
		case 0x6b: // LD L, E
			reg.write("L", reg.read("E"));
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
		case 0x86: // ADD A, (HL)
			ALU.instructADD(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x90: // SUB A, B
			ALU.instructSUB(reg.read("B"), false);
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
		case 0xB6: // OR A, (HL)
			ALU.instructOR(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0xB7: // OR A, A
			ALU.instructOR(reg.read("A"));
			cycles = 4;
			break;
		case 0xBE: // CP A, (HL)
			ALU.instructSUB(mmu.readByte(reg.read("HL")), true);
			cycles = 8;
			break;
		case 0xC1: // POP BC
			ControlFlow.instructPOP("BC");
			cycles = 12;
			break;
		case 0xC3: // JP u16
			ControlFlow.instructJP(fetchNextWord());
			cycles = 16;
			break;
		case 0xC4: // CALL NZ, u16
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
		case 0xC9: // RET
			ControlFlow.instructRET();
			cycles = 16;
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
		case 0xF3: // DI
			// TODO: implement when interrupts are implemented.
			cycles = 4;
			break;
		case 0xF5: // PUSH AF
			ControlFlow.instructPUSH("AF");
			cycles = 16;
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
		case 0xFE: // CP A, u8
			ALU.instructSUB(fetchNextByte(), true);
			cycles = 8;
			break;
		default:
			throw new IllegalArgumentException(String.format("Unhandled CPU instruction 0x%x", instruction));
		}
		cpuCycles += cycles;
		return cycles;
	}
	
	private int nextCBInstruction() {
		int instruction = fetchNextByte();
		int cycles;
		switch(instruction) {
		case 0x11: // RL C
			BitShift.instructRL("C");
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
		case 0x38: // SRL B
			BitShift.instructSRL("B");
			cycles = 8;
			break;
		case 0x7C: // BIT 7,H
			BitShift.instructBIT("H", 7);
			cycles = 8;
			break;
		default:
			throw new IllegalArgumentException(String.format("Unhandled CB instruction 0x%x", instruction));
		}
		return cycles;
	}

	public int fetchNextByte() {
		int result = mmu.readByte(reg.getPC());
		reg.incPC();
		return result;
	}
	
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
