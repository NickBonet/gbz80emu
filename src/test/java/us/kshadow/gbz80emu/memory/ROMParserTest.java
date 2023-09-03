package us.kshadow.gbz80emu.memory;

import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.memory.ROMParser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;

/**
 * Class to test basic functions of ROMParser. Uses Tetris ROM as baseline for now.
 * @author Nicholas Bonet
 */
class ROMParserTest {
	
	private static final ROMParser testGoodROM = new ROMParser();
	private static final ROMParser testBadROM = new ROMParser();
	
	private static final char[] testTitle = new char[] {
			0x54, 0x45, 0x54, 0x52, 0x49, 0x53, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
	};

	@BeforeAll
	public static void setup() {
		try {
			testGoodROM.loadROM("tetris.gb");
			testBadROM.loadROM("tetris-editedunit.gb");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests against a known good Tetris ROM for valid title, checksums, matching logo bitmap, and SGB function support.
	 */
	@Test
	void testValidROM() {
		assertEquals(String.copyValueOf(testTitle), testGoodROM.getTitle()); // test against title char[] above
		assertTrue(testGoodROM.isLogoValid()); // should be true with ANY valid ROM.
		assertFalse(testGoodROM.hasSgbFuncSupport()); // should be false for valid Tetris ROM.
		assertTrue(testGoodROM.isCartridgeValid());
		assertTrue(testGoodROM.isHeaderValid());
	}
	
	/**
	 * Tests against a malformed/edited Tetris ROM, to make sure checksum tests fail, as well as logo and title.
	 */
	@Test
	void testBadROM() {
		assertNotEquals(String.copyValueOf(testTitle), testBadROM.getTitle());
		assertFalse(testBadROM.isLogoValid());
		assertFalse(testBadROM.hasSgbFuncSupport());
		assertFalse(testBadROM.isCartridgeValid());
		assertFalse(testBadROM.isHeaderValid());
	}
}
