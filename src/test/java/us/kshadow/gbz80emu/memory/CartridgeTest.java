package us.kshadow.gbz80emu.memory;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test basic functions of Cartridge. Uses Tetris ROM as baseline for
 * now.
 * 
 * @author Nicholas Bonet
 */
class CartridgeTest {

	private static final Cartridge testCartridge = Cartridge.getInstance();

	private static final char[] testTitle = new char[]{0x54, 0x45, 0x54, 0x52, 0x49, 0x53, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00};

	/**
	 * Tests against a known good Tetris ROM for valid title, checksums, matching
	 * logo bitmap, and SGB function support.
	 */
	@Test
	void testValidROM() throws IOException {
		testCartridge.loadROM("tetris.gb");
		assertEquals(String.copyValueOf(testTitle), testCartridge.getTitle()); // test against title char[] above
		assertTrue(testCartridge.isLogoValid()); // should be true with ANY valid ROM.
		assertFalse(testCartridge.hasSgbFuncSupport()); // should be false for valid Tetris ROM.
		assertTrue(testCartridge.isCartridgeValid());
		assertTrue(testCartridge.isHeaderValid());
	}

	/**
	 * Tests against a malformed/edited Tetris ROM, to make sure checksum tests
	 * fail, as well as logo and title.
	 */
	@Test
	void testBadROM() throws IOException {
		testCartridge.loadROM("tetris-editedunit.gb");
		assertNotEquals(String.copyValueOf(testTitle), testCartridge.getTitle());
		assertFalse(testCartridge.isLogoValid());
		assertFalse(testCartridge.hasSgbFuncSupport());
		assertFalse(testCartridge.isCartridgeValid());
		assertFalse(testCartridge.isHeaderValid());
	}
}
