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
	
	/**
	 * JP - Handles absolute jump instruction.
	 * @param address - Address to set PC to.
	 */
	public static void instructJP(int address) {
		cpuReg.writeReg("PC", address, true);
	}
	
	/**
	 * Wrapper function for conditional jumps.
	 * @param opcode - Opcode of the conditional jump.
	 * @param address - Address to set PC to.
	 */
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
	
	/**
	 * JR - Handles relative jump, based on next byte in memory (signed).
	 * @param value - Next signed byte from memory.
	 */
	public static void instructJR(byte value) {
		cpuReg.writeReg("PC", cpuReg.getPC() + value, false);
	}
	
	/**
	 * Wrapper function for conditional relative. jumps.
	 * @param opcode - Opcode of the conditional relative jump.
	 * @param value - Next signed byte from memory.
	 */
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
	
	/**
	 * PUSH - Push register onto stack, then decrement SP by 2.
	 * @param register - Register to push onto stack.
	 */
	public static void instructPUSH(String register) {
		int value = cpuReg.getReg(register);
		int currentSP = cpuReg.getReg("SP");
		mmu.writeWord(cpuReg.getReg("SP"), value);
		cpuReg.writeReg("SP", currentSP - 2, false);
	}
	
	/**
	 * POP - Pop two bytes off the stack into specified register, then
	 * increment SP by 2.
	 * @param register - Register to store popped bytes in.
	 */
	public static void instructPOP(String register) {
		int currentSP = cpuReg.getReg("SP");
		int value = mmu.readWord(currentSP);
		cpuReg.writeReg(register, value, true);
		cpuReg.writeReg("SP", currentSP + 2, false);
	}
	
	/**
	 * RET - Pop two bytes from stack and jump to the address.
	 */
	public static void instructRET() {
		instructPOP("PC");
	}
	
	/**
	 * Wrapper function for conditional returns.
	 * @param opcode - Opcode of the conditional return.
	 */
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
	
	/**
	 * RETI - Similar to RET, except enable interrupts afterward.
	 */
	public static void instructRETI() {
		instructRET();
		// TODO: Enable interrupts here when they are implemented.
	}
	
	/**
	 * RST - Push PC onto stack then jump to one of the addresses based on opcode.
	 * @param opcode - Opcode for jump condition.
	 */
	public static void instructRST(int opcode) {
		instructPUSH("PC");
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
	
	/**
	 * CALL - Push PC onto the stack, then jump to the provided address.
	 * @param address - Address to jump to.
	 */
	public static void instructCALL(int address) {
		instructPUSH("PC");
		instructJP(address);
	}
	
	/**
	 * Wrapper function for conditional calls.
	 * @param opcode - Opcode of the conditional call.
	 * @param address - Address to jump to.
	 */
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
