package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.util.BitUtil;

/*
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
	
	/**
	 * Method to abstract the individual setters for registers, will be useful when mapping opcodes for similar instructions. (less duplication in those moments)
	 * @param register - String of register to write value to
	 * @param value - Data to be written to register
	 */
	public void writeToRegister(String register, int value) {
		switch(register) 
		{
			case "A":
				setA(value);
				break;
			case "B":
				setB(value);
				break;
			case "C":
				setC(value);
				break;
			case "D":
				setD(value);
				break;
			case "H":
				setH(value);
				break;
			case "E":
				setE(value);
				break;
			case "F":
				setF(value);
				break;
			case "L":
				setL(value);
				break;
			case "PC":
				setPc(value);
				break;
			case "SP":
				setSp(value);
				break;
			case "AF":
				setAF(value);
				break;
			case "BC":
				setBC(value);
				break;
			case "DE":
				setDE(value);
				break;
			case "HL":
				setHL(value);
				break;
		}
	}
	
	/**
	 * Method to abstract the individual getters for registers, will be useful when mapping opcodes for similar instructions. (less duplication in those moments)
	 * @param register - String of register to write value to
	 * @return Value of the register.
	 */
	public int getRegister(String register) {
		int regValue = 0;
		switch(register) 
		{
			case "A":
				regValue = getA();
				break;
			case "B":
				regValue = getB();
				break;
			case "C":
				regValue = getC();
				break;
			case "D":
				regValue = getD();
				break;
			case "H":
				regValue = getH();
				break;
			case "E":
				regValue = getE();
				break;
			case "F":
				regValue = getF();
				break;
			case "L":
				regValue = getL();
				break;
			case "PC":
				regValue = getPc();
				break;
			case "SP":
				regValue = getSp();
				break;
			case "AF":
				regValue = getAF();
				break;
			case "BC":
				regValue = getBC();
				break;
			case "DE":
				regValue = getDE();
				break;
			case "HL":
				regValue = getHL();
				break;
		}
		return regValue;
	}
	
	// Getters/setters for virtual 16-bit registers.
	public int getBC() {
		// Move the 8 bits of the B register to the far left, which leaves us 0s on the right side.
		// We then OR the bits from the C register against B, which effectively merges the two into a 16-bit number.
		return (b << 8) | c;
	}
	
	public int getAF() {
		return (a << 8) | this.flagRegister.flagsAsByte();
	}
	
	public int getDE() {
		return (d << 8) | e;
	}
	
	public int getHL() {
		return (h << 8) | l;
	}
	
	public void setBC(int bc) {
		BitUtil.checkIsWord(bc);
		b = bc >> 8;
		c = bc & 0xFF;
	}
	
	public void setAF(int af) {
		BitUtil.checkIsWord(af);
		a = af >> 8;
		this.flagRegister.flagsFromByte(af & 0xFF);
	}
	
	public void setDE(int de) {
		BitUtil.checkIsWord(de);
		d = de >> 8;
		e = de & 0xFF;
	}
	
	public void setHL(int hl) {
		BitUtil.checkIsWord(hl);
		h = hl >> 8;
		l = hl & 0xFF;
	}
	
	// Getters/setters for general registers.
	public int getA() {
		return a;
	}

	public void setA(int a) {
		BitUtil.checkIsByte(a);
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		BitUtil.checkIsByte(b);
		this.b = b;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		BitUtil.checkIsByte(d);
		this.d = d;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		BitUtil.checkIsByte(h);
		this.h = h;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		BitUtil.checkIsByte(c);
		this.c = c;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		BitUtil.checkIsByte(e);
		this.e = e;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		BitUtil.checkIsByte(l);
		this.l = l;
	}

	public int getF() {
		return this.flagRegister.flagsAsByte();
	}
	
	// return FlagRegister object in special cases
	public FlagRegister getFR() {
		return this.flagRegister;
	}

	public void setF(int f) {
		BitUtil.checkIsByte(f);
		this.flagRegister.flagsFromByte(f);
	}

	public int getPc() {
		return pc;
	}
	
	
	// Getters/setters for PC and SP registers
	public void setPc(int pc) {
		BitUtil.checkIsWord(pc);
		this.pc = pc;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		BitUtil.checkIsWord(sp);
		this.sp = sp;
	}
	
}
