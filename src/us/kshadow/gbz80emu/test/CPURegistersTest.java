package us.kshadow.gbz80emu.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import us.kshadow.gbz80emu.processor.CPURegisters;

class CPURegistersTest {
	
	private CPURegisters registers = CPURegisters.getInstance();

	@Test
	public void checkRegisterASizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setA(0x100);
		}); // check that we can't set above 0xFF
		
		registers.setA(0xFF);
		assertEquals(255, registers.getA()); // just double checking our boundaries with next two tests
		
		registers.setA(0x00);
		assertEquals(0, registers.getA());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setA(-1);
		}); // check that we can't go below 0x00
	}

	@Test
	public void checkRegisterBSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setB(0x100);
		}); 
		
		registers.setB(0xFF);
		assertEquals(255, registers.getB());
		
		registers.setB(0x00);
		assertEquals(0, registers.getB());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setB(-1);
		});
	}
	
	@Test
	public void checkRegisterDSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setD(0x100);
		}); 
		
		registers.setD(0xFF);
		assertEquals(255, registers.getD());
		
		registers.setD(0x00);
		assertEquals(0, registers.getD());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setD(-1);
		});
	}
	
	@Test
	public void checkRegisterHSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setH(0x100);
		}); 
		
		registers.setH(0xFF);
		assertEquals(255, registers.getH());
		
		registers.setH(0x00);
		assertEquals(0, registers.getH());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setH(-1);
		});
	}
	
	@Test
	public void checkRegisterFSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setF(0x100);
		}); 
		
		registers.setF(0xFF);
		assertEquals(240, registers.getF()); // won't be 0xFF, as the lower byte is discarded/all zeroes
		
		registers.setF(0x00);
		assertEquals(0, registers.getF());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setF(-1);
		});
	}
	
	@Test
	public void checkRegisterCSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setC(0x100);
		}); 
		
		registers.setC(0xFF);
		assertEquals(255, registers.getC());
		
		registers.setC(0x00);
		assertEquals(0, registers.getC());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setC(-1);
		});
	}
	
	@Test
	public void checkRegisterESizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setE(0x100);
		}); 
		
		registers.setE(0xFF);
		assertEquals(255, registers.getE());
		
		registers.setE(0x00);
		assertEquals(0, registers.getE());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setE(-1);
		});
	}
	
	@Test
	public void checkRegisterLSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setL(0x100);
		}); 
		
		registers.setL(0xFF);
		assertEquals(255, registers.getL());
		
		registers.setL(0x00);
		assertEquals(0, registers.getL());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setL(-1);
		});
	}
	
	// 16-bit register tests now
	@Test
	public void checkRegisterPcSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setPc(0x10000);
		}); 
		
		registers.setPc(0xFFFF);
		assertEquals(65535, registers.getPc());
		
		registers.setPc(0x00);
		assertEquals(0, registers.getPc());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setPc(-1);
		});
	}
	
	@Test
	public void checkRegisterSpSizeLimit() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setSp(0x10000);
		}); 
		
		registers.setSp(0xFFFF);
		assertEquals(65535, registers.getSp());
		
		registers.setSp(0x00);
		assertEquals(0, registers.getSp());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setSp(-1);
		});
	}
	
	// Testing size limits on our virtual 16-bit registers, in addition to values of individual registers.
	@Test
	public void checkRegisterAFSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setAF(0x10000);
		}); 
		
		registers.setAF(0xFFFF);
		assertEquals(65520, registers.getAF()); // should be stored as 0xFFF0, F register drops right-most 4 bits.
		assertEquals(0xFF, registers.getA());
		assertEquals(0xF0, registers.getF());
		
		registers.setAF(0x00);
		assertEquals(0, registers.getAF());
		assertEquals(0, registers.getA());
		assertEquals(0, registers.getF());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setAF(-1);
		});
	}
	
	@Test
	public void checkRegisterBCSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setBC(0x10000);
		}); 
		
		registers.setBC(0xFFFF);
		assertEquals(65535, registers.getBC());
		assertEquals(0xFF, registers.getB());
		assertEquals(0xFF, registers.getC());
		
		registers.setBC(0x00);
		assertEquals(0, registers.getBC());
		assertEquals(0, registers.getB());
		assertEquals(0, registers.getC());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setBC(-1);
		});
	}
	
	@Test
	public void checkRegisterDESizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setDE(0x10000);
		}); 
		
		registers.setDE(0xFFFF);
		assertEquals(65535, registers.getDE());
		assertEquals(0xFF, registers.getD());
		assertEquals(0xFF, registers.getE());
		
		registers.setDE(0x00);
		assertEquals(0, registers.getDE());
		assertEquals(0, registers.getD());
		assertEquals(0, registers.getE());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setDE(-1);
		});
	}
	
	@Test
	public void checkRegisterHLSizeAndValues() {
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setHL(0x10000);
		}); 
		
		registers.setHL(0xFFFF);
		assertEquals(65535, registers.getHL());
		assertEquals(0xFF, registers.getH());
		assertEquals(0xFF, registers.getL());
		
		registers.setHL(0x00);
		assertEquals(0, registers.getHL());
		assertEquals(0, registers.getH());
		assertEquals(0, registers.getL());
		
		assertThrows(IllegalArgumentException.class, ()->{
			registers.setHL(-1);
		});
	}
}
