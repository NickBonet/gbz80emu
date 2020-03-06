package us.kshadow.gbz80emu.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.processor.FlagRegister;

class FlagRegisterTest {
	
	private static final FlagRegister fr = FlagRegister.getInstance();
	
	@BeforeEach
	public void resetFR() {
		fr.setZ(false); 
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
	}

	@Test
	public void checkFlagAsByte() {
		fr.setZ(true); 
		fr.setC(true);
		assertEquals(0b10010000, fr.flagsAsByte()); // Verify correct bits for zero/carry flags are set
		
		fr.setZ(false);
		fr.setC(false);
		
		fr.setN(true);
		assertEquals(0b01000000, fr.flagsAsByte()); // sane as above for subtract flag
		fr.setH(true);
		assertEquals(0b01100000, fr.flagsAsByte()); // and half carry
	}
	
	@Test
	public void checkFlagFromByte() {
		fr.flagsFromByte(0xBF);
		assertEquals(0xB0, fr.flagsAsByte());
		assertEquals(true, fr.isZ());
		assertEquals(false, fr.isN());
		assertEquals(true, fr.isH());
		assertEquals(true, fr.isC());
		
		fr.flagsFromByte(0x0F);
		assertEquals(0x00, fr.flagsAsByte());
		assertEquals(false, fr.isZ());
		assertEquals(false, fr.isN());
		assertEquals(false, fr.isH());
		assertEquals(false, fr.isC());
		
		fr.flagsFromByte(0xDD);
		assertEquals(0xD0, fr.flagsAsByte());
		assertEquals(true, fr.isZ());
		assertEquals(true, fr.isN());
		assertEquals(false, fr.isH());
		assertEquals(true, fr.isC());
	}

}
