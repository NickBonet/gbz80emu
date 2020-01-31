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

}
