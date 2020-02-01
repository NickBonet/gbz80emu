package us.kshadow.gbz80emu.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.processor.CPURegisters;

class CPURegistersTest {
	
	private CPURegisters reg = CPURegisters.getInstance();

	@Test
	public void checkRegisterASizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("A", 0x100, false);
		}); // check that we can't set above 0xFF
		
		reg.writeReg("A", 0xFF, false);
		assertEquals(255, reg.getReg("A")); // just double checking our boundaries with next two tests
		
		reg.writeReg("A", 0x00, false);
		assertEquals(0, reg.getReg("A"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("A", -1, false);;
		}); // check that we can't go below 0x00
	}

	@Test
	public void checkRegisterBSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("B", 0x100, false);
		}); 
		
		reg.writeReg("B", 0xFF, false);
		assertEquals(255, reg.getReg("B"));
		
		reg.writeReg("B", 0x00, false);
		assertEquals(0, reg.getReg("B"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("B", -1, false);
		});
	}
	
	@Test
	public void checkRegisterDSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("D", 0x100, false);
		}); 
		
		reg.writeReg("D", 0xFF, false);
		assertEquals(255, reg.getReg("D"));
		
		reg.writeReg("D", 0x00, false);
		assertEquals(0, reg.getReg("D"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("D", -1, false);
		});
	}
	
	@Test
	public void checkRegisterHSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("H", 0x100, false);
		}); 
		
		reg.writeReg("H", 0xFF, false);
		assertEquals(255, reg.getReg("H"));
		
		reg.writeReg("H", 0x00, false);
		assertEquals(0, reg.getReg("H"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("H", -1, false);
		});
	}
	
	@Test
	public void checkRegisterFSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("F", 0x100, false);
		}); 
		
		reg.writeReg("F", 0xFF, false);
		assertEquals(240, reg.getReg("F")); // won't be 0xFF, as the lower byte is discarded/all zeroes
		
		reg.writeReg("F", 0x00, false);
		assertEquals(0, reg.getReg("F"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("F", -1, false);
		});
	}
	
	@Test
	public void checkRegisterCSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("C", 0x100, false);
		}); 
		
		reg.writeReg("C", 0xFF, false);
		assertEquals(255, reg.getReg("C"));
		
		reg.writeReg("C", 0x00, false);
		assertEquals(0, reg.getReg("C"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("C", -1, false);
		});
	}
	
	@Test
	public void checkRegisterESizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("E", 0x100, false);
		}); 
		
		reg.writeReg("E", 0xFF, false);
		assertEquals(255, reg.getReg("E"));
		
		reg.writeReg("E", 0x00, false);
		assertEquals(0, reg.getReg("E"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("E", -1, false);
		});
	}
	
	@Test
	public void checkRegisterLSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("L", 0x100, false);
		}); 
		
		reg.writeReg("L", 0xFF, false);
		assertEquals(255, reg.getReg("L"));
		
		reg.writeReg("L", 0x00, false);
		assertEquals(0, reg.getReg("L"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("L", -1, false);
		});
	}
	
	// 16-bit register tests now
	@Test
	public void checkRegisterPcSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("PC", 0x10000, true);
		}); 
		
		reg.writeReg("PC", 0xFFFE, true);
		assertEquals(65534, reg.getReg("PC"));
		
		reg.writeReg("PC", 0x00, true);
		assertEquals(0, reg.getReg("PC"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("PC", -1, true);
		});
	}
	
	@Test
	public void checkRegisterSpSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("SP", 0x10000, true);
		}); 
		
		reg.writeReg("SP", 0xFFFE, true);
		assertEquals(65534, reg.getReg("SP"));
		
		reg.writeReg("SP", 0x00, true);
		assertEquals(0, reg.getReg("SP"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("SP", -1, true);
		});
	}

	// Testing size limits on our virtual 16-bit registers, in addition to values of individual registers.
	@Test
	public void checkRegisterAFSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("AF", 0x10000, true);
		}); 
		
		reg.writeReg("AF", 0xFFFE, true);
		assertEquals(65520, reg.getReg("AF")); // should be stored as 0xFFF0, F register drops right-most 4 bits.
		assertEquals(0xFF, reg.getReg("A"));
		assertEquals(0xF0, reg.getReg("F"));
		
		reg.writeReg("AF", 0x00, true);
		assertEquals(0, reg.getReg("AF"));
		assertEquals(0, reg.getReg("A"));
		assertEquals(0, reg.getReg("F"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("AF", -1, true);
		});
	}
	
	@Test
	public void checkRegisterBCSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("BC", 0x10000, true);
		}); 
		
		reg.writeReg("BC", 0xFFFE, true);
		assertEquals(65534, reg.getReg("BC"));
		assertEquals(0xFF, reg.getReg("B"));
		assertEquals(0xFE, reg.getReg("C"));
		
		reg.writeReg("BC", 0x00, true);
		assertEquals(0, reg.getReg("BC"));
		assertEquals(0, reg.getReg("B"));
		assertEquals(0, reg.getReg("C"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("BC", -1, true);
		});
	}
	
	@Test
	public void checkRegisterDESizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("DE", 0x10000, true);
		}); 
		
		reg.writeReg("DE", 0xFFFE, true);
		assertEquals(65534, reg.getReg("DE"));
		assertEquals(0xFF, reg.getReg("D"));
		assertEquals(0xFE, reg.getReg("E"));
		
		reg.writeReg("DE", 0x00, true);
		assertEquals(0, reg.getReg("DE"));
		assertEquals(0, reg.getReg("D"));
		assertEquals(0, reg.getReg("E"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("DE", -1, true);
		});
	}
	
	@Test
	public void checkRegisterHLSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("HL", 0x10000, true);
		}); 
		
		reg.writeReg("HL", 0xFFFE, true);
		assertEquals(65534, reg.getReg("HL"));
		assertEquals(0xFF, reg.getReg("H"));
		assertEquals(0xFE, reg.getReg("L"));
		
		reg.writeReg("HL", 0x00, true);
		assertEquals(0, reg.getReg("HL"));
		assertEquals(0, reg.getReg("H"));
		assertEquals(0, reg.getReg("L"));
		
		assertThrows(IllegalArgumentException.class, ()->{
			reg.writeReg("HL", -1, true);
		});
	}
}
