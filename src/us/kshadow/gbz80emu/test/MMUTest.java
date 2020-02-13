package us.kshadow.gbz80emu.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.memory.ROMParser;

class MMUTest {
	
	private MMU mmu = MMU.getInstance();
	ROMParser testROM = new ROMParser("tetris.gb");
	
	@BeforeEach
	public void cleanup() {
		mmu.clearMemory();
	}
	
	@Test
	// Tests reading/writing properly to ROM bank arrays.
	public void testWriteThenReadROM() {
		mmu.loadROM(testROM.getRomAsArray());
		
		int[] readBank0 = new int[0x4000];
		int[] readBank1 = new int[0x4000];
		
		for (int i = 0; i < readBank0.length; i++) {
			readBank0[i] = mmu.readByte(i);
			readBank1[i] = mmu.readByte(i + 0x4000);
		}
		
		assertEquals(true, Arrays.equals(Arrays.copyOfRange(testROM.getRomAsArray(), 0x0000, 0x4000), readBank0));
		assertEquals(true, Arrays.equals(Arrays.copyOfRange(testROM.getRomAsArray(), 0x4000, 0x8000), readBank1));
	}

	public void testWriteByteROMFails() {
		for (int i = 0; i < testROM.getRomAsArray().length; i++) {
			mmu.writeByte(i, testROM.getRomAsArray()[i]);
		}
		
		int[] readROMTest = new int[0x8000];
		int[] emptyArr = new int[0x8000];
		Arrays.fill(emptyArr, 0);
		
		for (int i = 0; i < readROMTest.length; i++) {
			readROMTest[i] = mmu.readByte(i);
		}
		
		assertEquals(false, Arrays.equals(readROMTest, testROM.getRomAsArray()));
		assertEquals(true, Arrays.equals(emptyArr, readROMTest));
	}
}
