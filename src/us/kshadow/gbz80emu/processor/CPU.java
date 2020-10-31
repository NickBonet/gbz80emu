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
		// TODO: To be implemented later in the project.
		cpuCycles = 0;
		isRunning = true;
		mmu.toggleBootROM(true);
		logger.log(Level.INFO, "CPU execution started.");
	}
	
	public int nextInstruction() {
		/*
		if (reg.getPC() >= 0x100) {
			logger.log(Level.INFO, "BootROM execution complete.");
			reg.print();
			//isRunning = false;
			return 0;
		}*/
		int instruction = fetchNextByte();
		int cycles;
		//String loggerMsg = String.format("Executing instruction 0x%x", instruction);
		//logger.log(Level.INFO, loggerMsg);
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
			handle DAA here
			cycles = 4;
			break;*/
		case 0x20: // JR NZ,s8
		case 0x28: // JR Z, s8
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
		case 0x3D: // DEC A
			ALU.instructDECu8("A");
			cycles = 4;
			break;
		case 0x3E: // LD A, u8
			reg.write("A", fetchNextByte());
			cycles = 8;
			break;
		case 0x47: // LD B, A
			reg.write("B", reg.read("A"));
			cycles = 4;
			break;
		case 0x4F: // LD C, A
			reg.write("C", reg.read("A"));
			cycles = 4;
			break;
		case 0x57: // LD D, A
			reg.write("D", reg.read("A"));
			cycles = 4;
			break;
		case 0x67: // LD H, A
			reg.write("H", reg.read("A"));
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
		case 0x86: // ADD A, (HL)
			ALU.instructADD(mmu.readByte(reg.read("HL")));
			cycles = 8;
			break;
		case 0x90:
			ALU.instructSUB(reg.read("B"), false);
			cycles = 4;
			break;
		case 0xAF: // XOR A
			ALU.instructXOR(reg.read("A"));
			cycles = 4;
			break;
		case 0xBE:
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
		case 0xC5: // PUSH BC
			ControlFlow.instructPUSH("BC");
			cycles = 16;
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
		case 0xE0: // LD ($FF00+n), A
			mmu.writeByte(0xFF00 + fetchNextByte(), reg.read("A"));
			cycles = 12;
			break;
		case 0xE2: // LD ($FF00+C), A
			mmu.writeByte(0xFF00 + reg.read("C"), reg.read("A"));
			cycles = 8;
			break;
		case 0xEA: // LD (u16), A
			mmu.writeByte(fetchNextWord(), reg.read("A"));
			cycles = 16;
			break;
		case 0xF0: // LD A, ($FF00+n)
			reg.write("A", mmu.readByte(0xFF00 + fetchNextByte()));
			cycles = 12;
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
		//String loggerMsg = String.format("Executing CB instruction 0x%x", instruction);
		//logger.log(Level.INFO, loggerMsg);
		switch(instruction) {
		case 0x11: // RL C
			BitShift.instructRL("C");
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
