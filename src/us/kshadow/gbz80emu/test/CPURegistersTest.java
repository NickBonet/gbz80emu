package us.kshadow.gbz80emu.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.processor.CPURegisters;

class CPURegistersTest {
	
	private static final CPURegisters reg = CPURegisters.getInstance();
	
	@BeforeEach
	public void cleanup() {
		reg.clearRegisters();
	}

	@Test
	public void checkRegisterASizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("A", 0x100)
		); // check that we can't set above 0xFF
		
		reg.write("A", 0xFF);
		assertEquals(255, reg.read("A")); // just double checking our boundaries with next two tests
		
		reg.write("A", 0x00);
		assertEquals(0, reg.read("A"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("A", -1)
		); // check that we can't go below 0x00
	}

	@Test
	public void checkRegisterBSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("B", 0x100)
		); 
		
		reg.write("B", 0xFF);
		assertEquals(255, reg.read("B"));
		
		reg.write("B", 0x00);
		assertEquals(0, reg.read("B"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("B", -1)
		);
	}
	
	@Test
	public void checkRegisterDSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("D", 0x100)
		); 
		
		reg.write("D", 0xFF);
		assertEquals(255, reg.read("D"));
		
		reg.write("D", 0x00);
		assertEquals(0, reg.read("D"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("D", -1)
		);
	}
	
	@Test
	public void checkRegisterHSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("H", 0x100)
		); 
		
		reg.write("H", 0xFF);
		assertEquals(255, reg.read("H"));
		
		reg.write("H", 0x00);
		assertEquals(0, reg.read("H"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("H", -1)
		);
	}
	
	@Test
	public void checkRegisterFSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("F", 0x100)
		); 
		
		reg.write("F", 0xFF);
		assertEquals(240, reg.read("F")); // won't be 0xFF, as the lower byte is discarded/all zeroes
		
		reg.write("F", 0x00);
		assertEquals(0, reg.read("F"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("F", -1)
		);
	}
	
	@Test
	public void checkRegisterCSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("C", 0x100)
		); 
		
		reg.write("C", 0xFF);
		assertEquals(255, reg.read("C"));
		
		reg.write("C", 0x00);
		assertEquals(0, reg.read("C"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("C", -1)
		);
	}
	
	@Test
	public void checkRegisterESizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("E", 0x100)
		); 
		
		reg.write("E", 0xFF);
		assertEquals(255, reg.read("E"));
		
		reg.write("E", 0x00);
		assertEquals(0, reg.read("E"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("E", -1)
		);
	}
	
	@Test
	public void checkRegisterLSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("L", 0x100)
		); 
		
		reg.write("L", 0xFF);
		assertEquals(255, reg.read("L"));
		
		reg.write("L", 0x00);
		assertEquals(0, reg.read("L"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("L", -1)
		);
	}
	
	// 16-bit register tests now
	@Test
	public void checkRegisterPcSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("PC", 0x10000)
		); 
		
		reg.write("PC", 0xFFFE);
		assertEquals(65534, reg.read("PC"));
		
		reg.write("PC", 0x00);
		assertEquals(0, reg.read("PC"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("PC", -1)
		);
	}
	
	@Test
	public void checkRegisterSpSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("SP", 0x10000)
		); 
		
		reg.write("SP", 0xFFFE);
		assertEquals(65534, reg.read("SP"));
		
		reg.write("SP", 0x00);
		assertEquals(0, reg.read("SP"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("SP", -1)
		);
	}

	// Testing size limits on our virtual 16-bit registers, in addition to values of individual registers.
	@Test
	public void checkRegisterAFSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("AF", 0x10000)
		); 
		
		reg.write("AF", 0xFFFE);
		assertEquals(65520, reg.read("AF")); // should be stored as 0xFFF0, F register drops right-most 4 bits.
		assertEquals(0xFF, reg.read("A"));
		assertEquals(0xF0, reg.read("F"));
		
		reg.write("AF", 0x00);
		assertEquals(0, reg.read("AF"));
		assertEquals(0, reg.read("A"));
		assertEquals(0, reg.read("F"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("AF", -1)
		);
	}
	
	@Test
	public void checkRegisterBCSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("BC", 0x10000)
		); 
		
		reg.write("BC", 0xFFFE);
		assertEquals(65534, reg.read("BC"));
		assertEquals(0xFF, reg.read("B"));
		assertEquals(0xFE, reg.read("C"));
		
		reg.write("BC", 0x00);
		assertEquals(0, reg.read("BC"));
		assertEquals(0, reg.read("B"));
		assertEquals(0, reg.read("C"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("BC", -1)
		);
	}
	
	@Test
	public void checkRegisterDESizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("DE", 0x10000)
		); 
		
		reg.write("DE", 0xFFFE);
		assertEquals(65534, reg.read("DE"));
		assertEquals(0xFF, reg.read("D"));
		assertEquals(0xFE, reg.read("E"));
		
		reg.write("DE", 0x00);
		assertEquals(0, reg.read("DE"));
		assertEquals(0, reg.read("D"));
		assertEquals(0, reg.read("E"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("DE", -1)
		);
	}
	
	@Test
	public void checkRegisterHLSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("HL", 0x10000)
		); 
		
		reg.write("HL", 0xFFFE);
		assertEquals(65534, reg.read("HL"));
		assertEquals(0xFF, reg.read("H"));
		assertEquals(0xFE, reg.read("L"));
		
		reg.write("HL", 0x00);
		assertEquals(0, reg.read("HL"));
		assertEquals(0, reg.read("H"));
		assertEquals(0, reg.read("L"));
		
		assertThrows(IllegalArgumentException.class, ()->
			reg.write("HL", -1)
		);
	}
}
