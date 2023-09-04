package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;

/**
 * Contains implementation for instructions pertaining to control
 * flow/branching.
 * 
 * @author Nicholas Bonet
 *
 */
public class ControlFlow {

	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final FlagRegister fr = reg.getFR();
	private static final MMU mmu = MMU.getInstance();

	private ControlFlow() {
	}

	/**
	 * DI - Disables any potential interrupts
	 */
	public static void instructDI() {
		reg.toggleIME(false);
	}

	/**
	 * EI - Enable interrupts
	 */
	public static void instructEI() {
		reg.toggleIME(true);
	}

	/**
	 * JP - Handles absolute jump instruction.
	 * 
	 * @param address
	 *            - Address to set PC to.
	 */
	public static void instructJP(int address) {
		reg.write("PC", address);
	}

	/**
	 * Wrapper function for conditional jumps.
	 * 
	 * @param opcode
	 *            - Opcode of the conditional jump.
	 * @param address
	 *            - Address to set PC to.
	 */
	public static int instructCondJP(int opcode, int address) {
		int cycles = 12;
		switch (opcode) {
			case 0xC2 -> {
				if (!fr.isZ()) {
					instructJP(address);
					cycles += 4;
				}
			}
			case 0xCA -> {
				if (fr.isZ()) {
					instructJP(address);
					cycles += 4;
				}
			}
			case 0xD2 -> {
				if (!fr.isC()) {
					instructJP(address);
					cycles += 4;
				}
			}
			case 0xDA -> {
				if (fr.isC()) {
					instructJP(address);
					cycles += 4;
				}
			}
			default -> {
			}
		}
		return cycles;
	}

	/**
	 * JR - Handles relative jump, based on next byte in memory (signed).
	 * 
	 * @param value
	 *            - Next signed byte from memory.
	 */
	public static void instructJR(byte value) {
		reg.write("PC", reg.getPC() + value);
	}

	/**
	 * Wrapper function for conditional relative. jumps.
	 * 
	 * @param opcode
	 *            - Opcode of the conditional relative jump.
	 * @param value
	 *            - Next signed byte from memory.
	 */
	public static int instructCondJR(int opcode, byte value) {
		int cycles = 8;
		switch (opcode) {
			case 0x20 -> {
				if (!fr.isZ()) {
					instructJR(value);
					cycles += 4;
				}
			}
			case 0x28 -> {
				if (fr.isZ()) {
					instructJR(value);
					cycles += 4;
				}
			}
			case 0x30 -> {
				if (!fr.isC()) {
					instructJR(value);
					cycles += 4;
				}
			}
			case 0x38 -> {
				if (fr.isC()) {
					instructJR(value);
					cycles += 4;
				}
			}
			default -> {
			}
		}
		return cycles;
	}

	/**
	 * PUSH - Push register onto stack, then decrement SP by 2.
	 * 
	 * @param register
	 *            - Register to push onto stack.
	 */
	public static void instructPUSH(String register) {
		int currentSP = reg.read("SP");
		reg.write("SP", currentSP - 2);
		int value = reg.read(register);
		mmu.writeWord(reg.read("SP"), value);
	}

	/**
	 * POP - Pop two bytes off the stack into specified register, then increment SP
	 * by 2.
	 * 
	 * @param register
	 *            - Register to store popped bytes in.
	 */
	public static void instructPOP(String register) {
		int currentSP = reg.read("SP");
		int value = mmu.readWord(currentSP);
		reg.write(register, value);
		reg.write("SP", currentSP + 2);
	}

	/**
	 * RET - Pop two bytes from stack and jump to the address.
	 */
	public static void instructRET() {
		instructPOP("PC");
	}

	/**
	 * Wrapper function for conditional returns.
	 * 
	 * @param opcode
	 *            - Opcode of the conditional return.
	 */
	public static int instructCondRET(int opcode) {
		int cycles = 8;
		switch (opcode) {
			case 0xC0 -> {
				if (!fr.isZ()) {
					instructRET();
					cycles += 12;
				}
			}
			case 0xC8 -> {
				if (fr.isZ()) {
					instructRET();
					cycles += 12;
				}
			}
			case 0xD0 -> {
				if (!fr.isC()) {
					instructRET();
					cycles += 12;
				}
			}
			case 0xD8 -> {
				if (fr.isC()) {
					instructRET();
					cycles += 12;
				}
			}
			default -> {
			}
		}
		return cycles;
	}

	/**
	 * RETI - Similar to RET, except enable interrupts afterward.
	 */
	public static void instructRETI() {
		instructRET();
		instructEI();
	}

	/**
	 * RST - Push PC onto stack then jump to one of the addresses based on opcode.
	 * 
	 * @param opcode
	 *            - Opcode for jump condition.
	 */
	public static void instructRST(int opcode) {
		instructPUSH("PC");
		switch (opcode) {
			case 0xC7 -> reg.write("PC", 0x00);
			case 0xCF -> reg.write("PC", 0x08);
			case 0xD7 -> reg.write("PC", 0x10);
			case 0xDF -> reg.write("PC", 0x18);
			case 0xE7 -> reg.write("PC", 0x20);
			case 0xEF -> reg.write("PC", 0x28);
			case 0xF7 -> reg.write("PC", 0x30);
			case 0xFF -> reg.write("PC", 0x38);
			default -> {
			}
		}
	}

	/**
	 * CALL - Push PC onto the stack, then jump to the provided address.
	 * 
	 * @param address
	 *            - Address to jump to.
	 */
	public static void instructCALL(int address) {
		instructPUSH("PC");
		instructJP(address);
	}

	/**
	 * Wrapper function for conditional calls.
	 * 
	 * @param opcode
	 *            - Opcode of the conditional call.
	 * @param address
	 *            - Address to jump to.
	 */
	public static int instructCondCALL(int opcode, int address) {
		int cycles = 12;
		switch (opcode) {
			case 0xC4 -> {
				if (!fr.isZ()) {
					instructCALL(address);
					cycles += 12;
				}
			}
			case 0xCC -> {
				if (fr.isZ()) {
					instructCALL(address);
					cycles += 12;
				}
			}
			case 0xD4 -> {
				if (!fr.isC()) {
					instructCALL(address);
					cycles += 12;
				}
			}
			case 0xDC -> {
				if (fr.isC()) {
					instructCALL(address);
					cycles += 12;
				}
			}
			default -> {
			}
		}
		return cycles;
	}
}
