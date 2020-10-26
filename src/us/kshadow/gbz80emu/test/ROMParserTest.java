package us.kshadow.gbz80emu.test;

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
	
	private static ROMParser testGoodROM = new ROMParser();
	private static ROMParser testBadROM = new ROMParser();
	
	private static char[] testTitle = new char[] {
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
	public void testValidROM() {
		assertEquals(String.copyValueOf(testTitle), testGoodROM.getTitle()); // test against title char[] above
		assertEquals(true, testGoodROM.isLogoValid()); // should be true with ANY valid ROM.
		assertEquals(false, testGoodROM.hasSgbFuncSupport()); // should be false for valid Tetris ROM.
		assertEquals(true, testGoodROM.isCartridgeValid());
		assertEquals(true, testGoodROM.isHeaderValid());
	}
	
	/**
	 * Tests against a malformed/edited Tetris ROM, to make sure checksum tests fail, as well as logo and title.
	 */
	@Test
	public void testBadROM() {
		assertNotEquals(String.copyValueOf(testTitle), testBadROM.getTitle());
		assertEquals(false, testBadROM.isLogoValid()); 
		assertEquals(false, testBadROM.hasSgbFuncSupport());
		assertEquals(false, testBadROM.isCartridgeValid());
		assertEquals(false, testBadROM.isHeaderValid());
	}
}
