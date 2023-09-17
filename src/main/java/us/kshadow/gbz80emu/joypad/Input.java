package us.kshadow.gbz80emu.joypad;

/**
 * Enum representing button inputs & the bits they're mapped to in scope of the
 * joy pad register.
 */
public enum Input {

	START(3), SELECT(2), A(0), B(1), UP(2), DOWN(3), LEFT(1), RIGHT(0);

	private final int registerBit;

	Input(int bit) {
		registerBit = bit;
	}

	int getBit() {
		return registerBit;
	}
}
