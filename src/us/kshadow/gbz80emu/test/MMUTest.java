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
	
	@Test
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

}
