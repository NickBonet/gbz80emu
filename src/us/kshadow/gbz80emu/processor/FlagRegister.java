package us.kshadow.gbz80emu.processor;

import us.kshadow.gbz80emu.util.BitUtil;

/**
 * Abstraction for the flag register used in the CPU, flags set via boolean or by byte input. Flags can be read by boolean or converted to byte output.
 * @author Nicholas Bonet
 */

public class FlagRegister {
	
	private static final FlagRegister instance = new FlagRegister();
	
	private boolean z, n, h, c = false;
	
	private static int zPos = 7;
	private static int nPos = 6;
	private static int hPos = 5;
	private static int cPos = 4;
	
	private FlagRegister() { }
	
	public static FlagRegister getInstance() {
		return instance;
	}
	
	public int flagsAsByte() {
		int flagRegister = 0;
		
		if (z) {
			flagRegister |= 1 << zPos;
		}
		
		if (n) {
			flagRegister |= 1 << nPos;
		}
		
		if (h) {
			flagRegister |= 1 << hPos;
		}
		
		if (c) {
			flagRegister |= 1 << cPos;
		}
		
		return flagRegister;
	}
	
	public void flagsFromByte(int flags) {
		z = BitUtil.checkBitSet(flags, zPos);
		n = BitUtil.checkBitSet(flags, nPos);
		h = BitUtil.checkBitSet(flags, hPos);
		c = BitUtil.checkBitSet(flags, cPos);
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
