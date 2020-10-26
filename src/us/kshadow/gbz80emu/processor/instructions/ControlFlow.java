package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;

/**
 * Contains implementation for instructions pertaining to control flow/branching.
 * @author Nicholas Bonet
 *
 */
public class ControlFlow {

	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final FlagRegister fr = cpuReg.getFR();
	private static final MMU mmu = MMU.getInstance();
	
	private ControlFlow() { }
	
	// TODO: Implement HALT, STOP, DI, EI instructions, and finish RETI. (relies on interrupts impl.)
	
	// Handles absolute jump function.
	public static void instructJP(int address) {
		cpuReg.writeReg("PC", address, true);
	}
	
	// Handles conditional jumps.
	public static void instructCondJP(int opcode, int address) {
		switch(opcode) {
		case 0xC2:
			if (!fr.isZ()) {
				instructJP(address);
			}
			break;
		case 0xCA:
			if (fr.isZ()) {
				instructJP(address);
			}
			break;
		case 0xD2:
			if (!fr.isC()) {
				instructJP(address);
			}
			break;
		case 0xDA:
			if (fr.isC()) {
				instructJP(address);
			}
			break;
		default:
			break;
		}
	}
	
	// Handles relative jump, based on next byte in memory (signed).
	public static void instructJR(byte value) {
		cpuReg.writeReg("PC", cpuReg.getReg("PC") + value, false);
	}
	
	public static void instructCondJR(int opcode, byte value) {
		switch(opcode) {
		case 0x20:
			if (!fr.isZ()) {
				instructJR(value);
			}
			break;
		case 0x28:
			if (fr.isZ()) {
				instructJR(value);
			}
			break;
		case 0x30:
			if (!fr.isC()) {
				instructJR(value);
			}
			break;
		case 0x38:
			if (fr.isC()) {
				instructJR(value);
			}
			break;
		default:
			break;
		}
	}
	
	public static void instructPUSH(String register) {
		int value = cpuReg.getReg(register);
		int currentSP = cpuReg.getReg("SP");
		mmu.writeWord(cpuReg.getReg("SP"), value);
		cpuReg.writeReg("SP", currentSP - 2, false);
	}
	
	public static void instructPOP(String register) {
		int currentSP = cpuReg.getReg("SP");
		int value = mmu.readWord(currentSP);
		cpuReg.writeReg(register, value, true);
		cpuReg.writeReg("SP", currentSP + 2, false);
	}
	
	public static void instructRET() {
		instructPOP("PC");
	}
	
	public static void instructCondRET(int opcode) {
		switch(opcode) {
		case 0xC0:
			if (!fr.isZ()) {
				instructRET();
			}
			break;
		case 0xC8:
			if (fr.isZ()) {
				instructRET();
			}
			break;
		case 0xD0:
			if (!fr.isC()) {
				instructRET();
			}
			break;
		case 0xD8:
			if (fr.isC()) {
				instructRET();
			}
			break;
		default:
			break;
		}
	}
	
	public static void instructRETI() {
		instructRET();
		// TODO: Enable interrupts here when they are implemented.
	}
	
	public static void instructRST(int opcode) {
		instructPUSH("PC");
		// switch statement for opcodes
		switch(opcode) {
		case 0xC7:
			cpuReg.writeReg("PC", 0x00, false);
			break;
		case 0xCF:
			cpuReg.writeReg("PC", 0x08, false);
			break;
		case 0xD7:
			cpuReg.writeReg("PC", 0x10, false);
			break;
		case 0xDF:
			cpuReg.writeReg("PC", 0x18, false);
			break;
		case 0xE7:
			cpuReg.writeReg("PC", 0x20, false);
			break;
		case 0xEF:
			cpuReg.writeReg("PC", 0x28, false);
			break;
		case 0xF7:
			cpuReg.writeReg("PC", 0x30, false);
			break;
		case 0xFF:
			cpuReg.writeReg("PC", 0x38, false);
			break;
		default:
			break;
		}
	}
	
	public static void instructCALL(int address) {
		instructPUSH("PC");
		instructJP(address);
	}
	
	public static void instructCondCALL(int opcode, int address) {
		switch(opcode) {
		case 0xC4:
			if (!fr.isZ()) {
				instructCALL(address);
			}
			break;
		case 0xCC:
			if (fr.isZ()) {
				instructCALL(address);
			}
			break;
		case 0xD4:
			if (!fr.isC()) {
				instructCALL(address);
			}
			break;
		case 0xDC:
			if (fr.isC()) {
				instructCALL(address);
			}
			break;
		default:
			break;
		}
	}
}
