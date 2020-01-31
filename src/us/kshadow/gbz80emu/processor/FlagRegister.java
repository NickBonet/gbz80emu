package us.kshadow.gbz80emu.processor;

/*
 * Abstraction for the flag register used in the CPU, flags set via boolean or by byte input. Flags can be read by boolean or converted to byte output.
 * @author Nicholas Bonet
 */

public class FlagRegister {
	
	private static final FlagRegister instance = new FlagRegister();
	
	private boolean z, n, h, c = false;
	
	private int z_position = 7;
	private int n_position = 6;
	private int h_position = 5;
	private int c_position = 4;
	
	private FlagRegister() { }
	
	public static FlagRegister getInstance() {
		return instance;
	}
	
	public int flagsAsByte() {
		int flagRegister = 0;
		
		if (z) {
			flagRegister |= 1 << z_position;
		}
		
		if (n) {
			flagRegister |= 1 << n_position;
		}
		
		if (h) {
			flagRegister |= 1 << h_position;
		}
		
		if (c) {
			flagRegister |= 1 << c_position;
		}
		
		return flagRegister;
	}
	
	public void flagsFromByte(int flags) {
		z = this.checkBitSet(flags, z_position);
		n = this.checkBitSet(flags, n_position);
		h = this.checkBitSet(flags, h_position);
		c = this.checkBitSet(flags, c_position);
	}
	
	private boolean checkBitSet(int arg, int bitPos) {
		if (((arg >> bitPos) & 1) != 0) {
			return true;
		}
		
		return false;
	}

	public boolean isZ() {
		return z;
	}
	
	// Determines if Z should be set based on argument passed (i.e. result from instruction)
	public void setZ(int arg) {
		if (arg == 0) {
			this.z = true;
		} 
		
		else { 
			this.z = false;
		}
	}

	public boolean isN() {
		return n;
	}

	public void setN(boolean n) {
		this.n = n;
	}

	public boolean isH() {
		return h;
	}

	public void setH(boolean h) {
		this.h = h;
	}

	public boolean isC() {
		return c;
	}

	public void setC(boolean c) {
		this.c = c;
	}
	
}
