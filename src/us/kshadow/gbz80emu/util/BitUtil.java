package us.kshadow.gbz80emu.util;

/*
 * Misc. class encompassing various bit manipulation methods used throughout the project.
 * @author Nicholas Bonet
 */

public class BitUtil {

	// Verification method to check if a value is 8-bit before placing into a register.
	public static void checkIsByte(int arg) {
		if (!(arg >= 0 && arg <= 0xFF)) {
			throw new IllegalArgumentException("Argument is not a valid byte.");
		}
	}
	
	// Same as above, but for 16-bit numbers.
	public static void checkIsWord(int arg) {
		if (!(arg >= 0 && arg <= 0xFFFF)) {
			throw new IllegalArgumentException("Argument is not a valid word.");
		}
	}

	public static boolean checkBitSet(int arg, int bitPos) {
		if (((arg >> bitPos) & 1) != 0) {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkHalfCarryAdd(int a, int b) {
		int innerExp = (a & 0xF) + (b & 0xF); // mask off upper halves of bytes as we're really only interested in 4 bit addition for this
		return (innerExp & 0x10) == 0x10; // AND the sum above against 0x10 (which in short checks if the 4th bit (0-7) was set,
											// and return whether the result equals 0x10 or not
	}

	
	public static boolean checkHalfCarrySub(int a, int b) {
		return ((a & 0xF) < (b & 0xF)); // very basic carry check, if lower half of first number if less than lower half of second, a half carry will be required.
	}
}
