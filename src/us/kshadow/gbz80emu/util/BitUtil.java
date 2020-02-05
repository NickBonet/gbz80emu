package us.kshadow.gbz80emu.util;

/**
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
	
	public static int setBit(int arg, int bitPos) {
		if (checkBitSet(arg, bitPos)) {
			return arg &= ~(1 << bitPos);
		} else { return arg | (1 << bitPos); }
	}
	
	/**
	 * @param carryFlag - For ADC: If carry flag is 1, set true, false if 0
	 */
	public static boolean checkHalfCarryAdd(int a, int b, boolean carryFlag) {
		int innerExp = (a & 0xF) + (b & 0xF) + (carryFlag ? 1 : 0); // mask off upper halves of bytes as we're really only interested in 4 bit addition for this
		return (innerExp & 0x10) == 0x10; // AND the sum above against 0x10 (which in short checks if the 4th bit (0-7) was set,
											// and return whether the result equals 0x10 or not
	}

	/**
	 *  @param carryFlag - For SBC: If carry flag is 1, set true, false if 0
	 */
	public static boolean checkHalfCarrySub(int a, int b, boolean carryFlag) {
		return ((a & 0xF) < ((b & 0xF) + (carryFlag ? 1 : 0))); // very basic carry check, if lower half of first number if less than lower half of second, a half carry will be required.
	}
	
	/**
	 * @param carryFlag - For ADC: If carry flag is 1, set true, false if 0
	 */
	public static boolean checkCarryAdd(int a, int b, boolean carryFlag) {
		return ((a + b + (carryFlag ? 1 : 0)) > 0xFF);
	}
	
	/**
	 * @param carryFlag - For SBC: If carry flag is 1, set true, false if 0
	 */
	public static boolean checkCarrySub(int a, int b, boolean carryFlag) {
		return (b - (carryFlag ? 1 : 0) > a);
	}
	
}
