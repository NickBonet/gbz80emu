package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.util.BitUtil;

/**
 * Abstraction for the flag register used in the CPU, flags set via boolean or by byte input. Flags can be read by boolean or converted to byte output.
 * @author Nicholas Bonet
 */

public class FlagRegister {
	
	private static final FlagRegister instance = new FlagRegister();
	
	private boolean z, n, h, c = false;
	
	private static final int Z_POS = 7;
	private static final int N_POS = 6;
	private static final int H_POS = 5;
	private static final int C_POS = 4;
	
	private FlagRegister() { }
	
	public static FlagRegister getInstance() {
		return instance;
	}
	
	public int getFlagsAsByte() {
		int flagRegister = 0;
		
		if (z) {
			flagRegister |= 1 << Z_POS;
		}
		
		if (n) {
			flagRegister |= 1 << N_POS;
		}
		
		if (h) {
			flagRegister |= 1 << H_POS;
		}
		
		if (c) {
			flagRegister |= 1 << C_POS;
		}
		
		return flagRegister;
	}
	
	public void setFlagsFromByte(int flags) {
		z = BitUtil.checkBitSet(flags, Z_POS);
		n = BitUtil.checkBitSet(flags, N_POS);
		h = BitUtil.checkBitSet(flags, H_POS);
		c = BitUtil.checkBitSet(flags, C_POS);
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
