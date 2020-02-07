package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.util.BitUtil;

/**
 * Represents the 8 8-bit registers and the 4 16-bit "virtual" registers implemented in the Gameboy Z80.
 * Also represents the program counter and stack pointer registers.
 * @author Nicholas Bonet
 */

public class CPURegisters {
	
	private static final CPURegisters instance = new CPURegisters();
	
	// The 8 basic 8-bit CPU registers.
	private int a, b, d, h, c, e, l;
	
	private FlagRegister flagRegister = FlagRegister.getInstance();
	
	// Program counter register, holds address data for next instruction to be executed by the CPU.
	private int pc;
	
	// Stack pointer, holds starting addr. of the stack area in memory.
	private int sp;
	
	private CPURegisters() { }
	
	public static CPURegisters getInstance() {
		return instance;
	}
	
	// Simply reset all registers to 0x00.
	public void clearRegs() {
		a = b = d = h = c = e = l = pc = sp = 0;
		flagRegister.flagsFromByte(0x00);
	}
	
	// Simple method for printing register values to console.
	public void printReg() {
		System.out.println(String.format("A: 0x%x  B: 0x%x  D: 0x%x  H: 0x%x  C: 0x%x  E: 0x%x  L: 0x%x  PC: 0x%x  SP: 0x%x  FR: 0x%x", 
				a, b, d, h, c, e, l, pc, sp, flagRegister.flagsAsByte()));
	}
	
	/**
	 * Method to abstract the individual setters for registers, will be useful when mapping opcodes for similar instructions. (less duplication in those moments)
	 * @param register - String of register to write value to.
	 * @param value - Data to be written to register.
	 * @param word - True if register and value are 16-bit, false if 8-bit register and value.
	 */
	public void writeReg(String register, int value, boolean word) {
		if (word) { BitUtil.checkIsWord(value); }
		else { BitUtil.checkIsByte(value); }
		switch(register.toUpperCase()) 
		{
			case "A":
				this.a = value;
				break;
			case "B":
				this.b = value;
				break;
			case "C":
				this.c = value;
				break;
			case "D":
				this.d = value;
				break;
			case "H":
				this.h = value;
				break;
			case "E":
				this.e = value;
				break;
			case "F":
				flagRegister.flagsFromByte(value);
				break;
			case "L":
				this.l = value;
				break;
			case "PC":
				this.pc = value;
				break;
			case "SP":
				this.sp = value;
				break;
			case "AF":
				a = value >> 8;
				flagRegister.flagsFromByte(value & 0xFF);
				break;
			case "BC":
				b = value >> 8;
				c = value & 0xFF;
				break;
			case "DE":
				d = value >> 8;
				e = value & 0xFF;
				break;
			case "HL":
				h = value >> 8;
				l = value & 0xFF;
				break;
			default:
				throw new IllegalArgumentException("Register write: Invalid register " + register);
		}
	}
	
	/**
	 * Method to abstract the individual getters for registers, will be useful when mapping opcodes for similar instructions. (less duplication in those moments)
	 * @param register - String of register to read value from.
	 * @return Value of the register.
	 */
	public int getReg(String register) {
		int regValue = 0;
		switch(register.toUpperCase()) 
		{
			case "A":
				regValue = this.a;
				break;
			case "B":
				regValue = this.b;
				break;
			case "C":
				regValue = this.c;
				break;
			case "D":
				regValue = this.d;
				break;
			case "H":
				regValue = this.h;
				break;
			case "E":
				regValue = this.e;
				break;
			case "F":
				regValue = flagRegister.flagsAsByte();
				break;
			case "L":
				regValue = this.l;
				break;
			case "PC":
				regValue = this.pc;
				break;
			case "SP":
				regValue = this.sp;
				break;
			case "AF":
				// Move the 8 bits of the B register to the far left, which leaves us 0s on the right side.
				// We then OR the bits from the C register against B, which effectively merges the two into a 16-bit number.
				regValue = (a << 8) | flagRegister.flagsAsByte();
				break;
			case "BC":
				regValue = (b << 8) | c;
				break;
			case "DE":
				regValue = (d << 8) | e;
				break;
			case "HL":
				regValue = (h << 8) | l;
				break;
			default:
				throw new IllegalArgumentException("Register read: Invalid register " + register);
		}
		return regValue;
	}
	
	// return FlagRegister object in special cases
	public FlagRegister getFR() {
		return flagRegister;
	}
}
