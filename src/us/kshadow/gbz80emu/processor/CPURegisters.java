package us.kshadow.gbz80emu.processor;

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
	
	// Verification method to check if a value is 8-bit before placing into a register.
	public void checkIsByte(int arg) {
		if (!(arg >= 0 && arg <= 0xFF)) {
			throw new IllegalArgumentException("Argument is not a valid byte.");
		}
	}
	
	// Same as above, but for 16-bit numbers.
	public void checkIsWord(int arg) {
		if (!(arg >= 0 && arg <= 0xFFFF)) {
			throw new IllegalArgumentException("Argument is not a valid word.");
		}
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
		this.checkIsWord(bc);
		b = bc >> 8;
		c = bc & 0xFF;
	}
	
	public void setAF(int af) {
		this.checkIsWord(af);
		a = af >> 8;
		this.flagRegister.flagsFromByte(af & 0xFF);
	}
	
	public void setDE(int de) {
		this.checkIsWord(de);
		d = de >> 8;
		e = de & 0xFF;
	}
	
	public void setHL(int hl) {
		this.checkIsWord(hl);
		h = hl >> 8;
		l = hl & 0xFF;
	}
	
	// Getters/setters for general registers.
	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.checkIsByte(a);
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.checkIsByte(b);
		this.b = b;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.checkIsByte(d);
		this.d = d;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.checkIsByte(h);
		this.h = h;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.checkIsByte(c);
		this.c = c;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.checkIsByte(e);
		this.e = e;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.checkIsByte(l);
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
		this.checkIsByte(f);
		this.flagRegister.flagsFromByte(f);
	}

	public int getPc() {
		return pc;
	}
	
	
	// Getters/setters for PC and SP registers
	public void setPc(int pc) {
		this.checkIsWord(pc);
		this.pc = pc;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		this.checkIsWord(sp);
		this.sp = sp;
	}
	
}
