package us.kshadow.gbz80emu.processor;

public class FlagRegister {
	
	private boolean z, n, h, c;
	
	private int z_position = 7;
	private int n_position = 6;
	private int h_position = 5;
	private int c_position = 4;
	
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

	public void setZ(boolean z) {
		this.z = z;
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
