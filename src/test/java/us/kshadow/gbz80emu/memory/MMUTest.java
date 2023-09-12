package us.kshadow.gbz80emu.memory;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MMUTest {

	private static final MMU mmu = MMU.getInstance();
	private final Cartridge testROM = Cartridge.getInstance();

	public void setup() {
		try {
			testROM.loadROM("tetris.gb");
			mmu.toggleBootROM(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@BeforeEach
	public void cleanup() {
		mmu.clearMemory();
	}

	/**
	 * Tests for proper reading/writing to ROM bank arrays.
	 */
	@Test
	void testWriteThenReadROM() {
		setup();

		int[] readBank0 = new int[0x4000];
		int[] readBank1 = new int[0x4000];

		for (int i = 0; i < readBank0.length; i++) {
			readBank0[i] = mmu.readByte(i);
			readBank1[i] = mmu.readByte(i + 0x4000);
		}

		assertArrayEquals(Arrays.copyOfRange(testROM.getROM(), 0x0000, 0x4000), readBank0);
		assertArrayEquals(Arrays.copyOfRange(testROM.getROM(), 0x4000, 0x8000), readBank1);
	}

	/**
	 * Tests writeByte() and validates that it doesn't write to ROM arrays.
	 * (0x0000-0x7FFF)
	 */
	@Test
	void testWriteByteROMFails() {
		setup();
		for (int i = 0; i < testROM.getROM().length; i++) {
			mmu.writeByte(i, 0x00);
		}

		int[] readROMTest = new int[0x8000];
		int[] emptyArr = new int[0x8000];
		Arrays.fill(emptyArr, 0);

		for (int i = 0; i < readROMTest.length; i++) {
			readROMTest[i] = mmu.readByte(i);
		}

		assertArrayEquals(readROMTest, testROM.getROM());
		assertFalse(Arrays.equals(readROMTest, emptyArr));
	}

	/**
	 * Verifies that the unused range (0xFEA0-0xFEFF) cannot be written to/read
	 * from.
	 */
	@Test
	void testNoReadWriteUnusableRange() {
		mmu.writeByte(0xFEA0, 25);
		mmu.writeByte(0xFEFF, 82);
		assertEquals(0, mmu.readByte(0xFEA0));
		assertEquals(0, mmu.readByte(0xFEFF));
	}

	/**
	 * Asserts that exceptions are thrown if address out of normal range is
	 * specified for read/write.
	 */
	@Test
	void outOfRangeTest() {
		assertThrows(IllegalArgumentException.class, () -> mmu.readByte(0x10000));
		assertThrows(IllegalArgumentException.class, () -> mmu.writeByte(0x10000, 21));
		assertThrows(IllegalArgumentException.class, () -> mmu.readWord(0x10000));
		assertThrows(IllegalArgumentException.class, () -> mmu.writeWord(0x10000, 21));
	}

	/**
	 * Tests readWord/writeWord, validating that they read as/write as little
	 * endian.
	 */
	@Test
	void readWriteWordTest() {
		mmu.writeWord(0xC000, 0x150);
		assertEquals(0x150, mmu.readWord(0xC000));
	}

	/**
	 * FIlls each address range (besides ROM) with a differing value, and reads it
	 * back. Also tests that echo RAM implementation works properly.
	 */
	@Test
	void fillAndRead() {
		int[] vRam = new int[0x2000];
		int[] vRamFull = new int[0x2000];

		int[] extRam = new int[0x8000];
		int[] extRamFull = new int[0x8000];

		int[] wRam = new int[0x2000];
		int[] wRamFull = new int[0x2000];

		int[] echoRam = new int[0x1E00];

		int[] oam = new int[0xA0];
		int[] oamFull = new int[0xA0];

		int[] zeroPage = new int[0x7F];
		int[] zeroPageFull = new int[0x7F];

		Arrays.fill(vRamFull, 0x59);
		// Arrays.fill(extRamFull, 0x74);
		Arrays.fill(wRamFull, 0x87);
		Arrays.fill(oamFull, 0x22);
		Arrays.fill(zeroPageFull, 0x22);

		for (int i = 0; i < 0x2000; i++) {
			mmu.writeByte(0x8000 + i, 0x59); // VRAM
			vRam[i] = mmu.readByte(0x8000 + i);
			mmu.writeByte(0xA000 + i, 0x74); // Ext RAM
			extRam[i] = mmu.readByte(0xA000 + i);
			extRamFull[i] = mmu.readByte(0xA000 + i);
			mmu.writeByte(0xC000 + i, 0x87); // Working RAM
			wRam[i] = mmu.readByte(0xC000 + i);
		}

		for (int i = 0; i < 0x1E00; i++) {
			echoRam[i] = mmu.readByte(0xE000 + i);
		}

		assertArrayEquals(vRam, vRamFull);
		assertArrayEquals(extRam, extRamFull);
		assertArrayEquals(wRam, wRamFull);
		assertArrayEquals(Arrays.copyOfRange(wRam, 0, 0x1E00), echoRam);

		for (int i = 0; i < 0x200; i++) {
			mmu.writeByte(0xFE00 + i, 0x22); // OAM, zeroPage
		}

		for (int i = 0; i < 0xA0; i++) {
			oam[i] = mmu.readByte(0xFE00 + i);
		}
		for (int i = 0; i < 0x7F; i++) {
			zeroPage[i] = mmu.readByte(0xFF80 + i);
		}

		assertArrayEquals(oam, oamFull);
		assertArrayEquals(zeroPage, zeroPageFull);
	}
}
