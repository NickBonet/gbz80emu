package us.kshadow.gbz80emu.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kshadow.gbz80emu.util.BitUtil;

/**
 * Represents the 8 8-bit registers and the 4 16-bit "virtual" registers
 * implemented in the Game Boy Z80. Also represents the program counter and
 * stack pointer registers.
 * 
 * @author Nicholas Bonet
 */

@SuppressWarnings("java:S6548")
public class CPURegisters {

	private static final CPURegisters instance = new CPURegisters();
	private static final Logger logger = LoggerFactory.getLogger(CPURegisters.class);

	// The 8 basic 8-bit CPU registers.
	private int a;
	private int b;
	private int d;
	private int h;
	private int c;
	private int e;
	private int l;

	// The interrupt master enable flag
	private boolean ime;

	// Instance of the flag register.
	private static final FlagRegister flagRegister = FlagRegister.getInstance();

	// Program counter register, holds address data for next instruction to be
	// executed by the CPU.
	private int pc;

	// Stack pointer, holds starting addr. of the stack area in memory.
	private int sp;

	private CPURegisters() {
	}

	/**
	 * Return the singleton instance of the CPU's registers.
	 * 
	 * @return This CPURegisters instance.
	 */
	public static CPURegisters getInstance() {
		return instance;
	}

	/**
	 * Simply reset all registers to 0x00.
	 */
	public void clearRegisters() {
		a = b = d = h = c = e = l = pc = sp = 0;
		flagRegister.setFlagsFromByte(0x00);
	}

	/**
	 * Simple method for printing register values to console.
	 */
	public void print() {
		String registers = String.format(
				"A: 0x%X  B: 0x%X  D: 0x%X  H: 0x%X  C: 0x%X  E: 0x%X  L: 0x%X  PC: 0x%X  SP: 0x%X", a, b, d, h, c, e,
				l, pc, sp);
		String flags = String.format("[FR]: Zero: %s, Negative: %s, Carry: %s, HalfCarry: %s", getFR().isZ(),
				getFR().isN(), getFR().isC(), getFR().isH());
		logger.debug("{} \n {}", registers, flags);
	}

	/**
	 * Method to abstract the individual setters for registers, will be useful when
	 * mapping opcodes for similar instructions. (less duplication in those moments)
	 * 
	 * @param register
	 *            - String of register to write value to.
	 * @param value
	 *            - Data to be written to register.
	 */
	public void write(String register, int value) {
		boolean word = register.length() > 1;
		if (word) {
			BitUtil.checkIsWord(value);
		} else {
			BitUtil.checkIsByte(value);
		}
		switch (register.toUpperCase()) {
			case "A" -> this.a = value;
			case "B" -> this.b = value;
			case "C" -> this.c = value;
			case "D" -> this.d = value;
			case "H" -> this.h = value;
			case "E" -> this.e = value;
			case "F" -> flagRegister.setFlagsFromByte(value);
			case "L" -> this.l = value;
			case "PC" -> this.pc = value;
			case "SP" -> this.sp = value;
			case "AF" -> {
				a = value >> 8;
				flagRegister.setFlagsFromByte(value & 0xFF);
			}
			case "BC" -> {
				b = value >> 8;
				c = value & 0xFF;
			}
			case "DE" -> {
				d = value >> 8;
				e = value & 0xFF;
			}
			case "HL" -> {
				h = value >> 8;
				l = value & 0xFF;
			}
			default -> throw new IllegalArgumentException("Register write: Invalid register " + register);
		}
	}

	/**
	 * Method to abstract the individual getters for registers, will be useful when
	 * mapping opcodes for similar instructions. (less duplication in those moments)
	 * 
	 * @param register
	 *            - String of register to read value from.
	 * @return Value of the register.
	 */
	public int read(String register) {
		return switch (register.toUpperCase()) {
			case "A" -> this.a;
			case "B" -> this.b;
			case "C" -> this.c;
			case "D" -> this.d;
			case "H" -> this.h;
			case "E" -> this.e;
			case "F" -> flagRegister.getFlagsAsByte();
			case "L" -> this.l;
			case "PC" -> this.pc;
			case "SP" -> this.sp;
			case "AF" ->
				// Move the 8 bits of the B register to the far left, which leaves us 0s on the
				// right side.
				// We then OR the bits from the C register against B, which effectively merges
				// the two into a 16-bit number.
				(a << 8) | flagRegister.getFlagsAsByte();
			case "BC" -> (b << 8) | c;
			case "DE" -> (d << 8) | e;
			case "HL" -> (h << 8) | l;
			default -> throw new IllegalArgumentException("Register read: Invalid register " + register);
		};
	}

	/**
	 * Sets the initial values of the registers as if the firmware has executed on
	 * the GB.
	 */
	public void setInitValues() {
		this.write("AF", 0x01B0);
		this.write("BC", 0x0013);
		this.write("DE", 0x00D8);
		this.write("HL", 0x014D);
		this.write("SP", 0xFFFE);
		this.write("PC", 0x100);
	}

	/**
	 * Increases PC by 1.
	 */
	public void incPC() {
		this.pc++;
	}

	/**
	 * Get the current value of PC.
	 * 
	 * @return The current value of PC.
	 */
	public int getPC() {
		return pc;
	}

	/**
	 * Returns the FlagRegister object.
	 * 
	 * @return see above.
	 */
	public FlagRegister getFR() {
		return flagRegister;
	}

	public boolean getIME() {
		return ime;
	}

	public void toggleIME(boolean ime) {
		this.ime = ime;
	}

}
