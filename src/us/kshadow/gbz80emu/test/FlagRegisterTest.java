package us.kshadow.gbz80emu.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.After;
import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.processor.FlagRegister;

class FlagRegisterTest {
	
	private final FlagRegister FR = new FlagRegister();
	
	@After
	public void resetFR() {
		FR.setZ(false);
		FR.setC(false);
		FR.setN(false);
		FR.setH(false);
	}

	@Test
	public void checkFlagAsByte() {
		FR.setZ(true);
		FR.setC(true);
		assertEquals(0b10010000, FR.flagsAsByte()); // Verify correct bits for zero/carry flags are set
		
		FR.setZ(false);
		FR.setC(false);
		
		FR.setN(true);
		assertEquals(0b01000000, FR.flagsAsByte()); // sane as above for subtract flag
		FR.setH(true);
		assertEquals(0b01100000, FR.flagsAsByte()); // and half carry
	}
	
	@Test
	public void checkFlagFromByte() {
		FR.flagsFromByte(0xBF);
		assertEquals(0xB0, FR.flagsAsByte());
		assertEquals(true, FR.isZ());
		assertEquals(false, FR.isN());
		assertEquals(true, FR.isH());
		assertEquals(true, FR.isC());
		
		FR.flagsFromByte(0x0F);
		assertEquals(0x00, FR.flagsAsByte());
		assertEquals(false, FR.isZ());
		assertEquals(false, FR.isN());
		assertEquals(false, FR.isH());
		assertEquals(false, FR.isC());
		
		FR.flagsFromByte(0xDD);
		assertEquals(0xD0, FR.flagsAsByte());
		assertEquals(true, FR.isZ());
		assertEquals(true, FR.isN());
		assertEquals(false, FR.isH());
		assertEquals(true, FR.isC());
	}

}
