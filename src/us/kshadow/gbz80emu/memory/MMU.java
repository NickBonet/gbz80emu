package us.kshadow.gbz80emu.memory;

import java.io.IOException;
import java.util.Arrays;

import us.kshadow.gbz80emu.graphics.GPU;
import us.kshadow.gbz80emu.util.BitUtil;

/**
 * A basic memory management unit abstraction.
 * @author Nicholas Bonet
 */

public class MMU {
	
	private static final MMU instance = new MMU();
	private static final GPU gpu = GPU.getInstance();
	
	// Gets switched out at end of actual Gameboy boot up, when $FF50 is written to.
	private int[] bootRom = new int[0xFF];
	private boolean bootRomEnabled = true;
	
	// 0x0000 - 0x3FFF - ROM Bank 0
	// This section of memory is mapped to the first 16kb of a GB ROM.
	private int[] romBank0 = new int[0x4000];
	
	// 0x4000 - 0x7FFF - ROM Bank 1
	// This section can be dynamically switched to another section of a GB ROM if it's > 32kb.
	// Else, this is just the 2nd bank of a 32kb ROM.
	private int[] romBank1 = new int[0x4000];
	
	// 0x8000 - 0x9FFF - VRAM (will be properly segmented later on)
	private int[] videoRam = new int[0x2000];
	
	// 0xA000 - 0xBFFF - External Cart RAM
	private int[] extRam = new int[0x2000];
	
	// 0xC000 - 0xDFFF - Working RAM
	// 0xE000 - 0xFDFF - Shadow of working RAM (last 512 bytes aren't shadowed)
	private int[] workRam = new int[0x2000];
	
	// 0xFE00 - 0xFE9F - Sprite Attribute Table (OAM)
	private int[] oam = new int[0xA0];
	
	// 0xFEA0 - 0xFEFF - unused range
	// TODO: 0xFF00 - 0xFF7F - I/O registers
	private int joyPadRegister = 0xF1;
	
	// 0xFF80 - 0xFFFE - Zero Page RAM
	private int[] zeroPage = new int[0x7F];

	private int interruptFlag = 0; // 0xFF0F
	private int interruptEnable = 0; // 0xFFFF
	
	/**
	 * MMU constructor. Simply loads the boot ROM.
	 */
	private MMU() { 
		ROMParser bootROM = new ROMParser();
		try {
			bootROM.loadROM("dmg_boot.bin");
			loadBootROM(bootROM.getROMAsArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the singleton instance of the MMU.
	 * @return This MMU instance.
	 */
	public static MMU getInstance() {
		return instance;
	}
	
	/**
	 * Handles reading a byte from the correct region of memory, based on memory address.
	 * @param address - Address of the byte to read from memory.
	 * @return The byte from memory.
	 */
	public int readByte(int address) {
		BitUtil.checkIsWord(address);
		switch(address & 0xF000) 
		{
			case 0x0000:
				if (address < 0x100) {
					return bootRomEnabled ? bootRom[address] : romBank0[address];
				}
				return romBank0[address];
			case 0x1000:
			case 0x2000:
			case 0x3000:
				return romBank0[address];
			
			case 0x4000:
			case 0x5000:
			case 0x6000:
			case 0x7000:
				return romBank1[address & 0x3FFF];
			
			case 0x8000:
			case 0x9000:
				return videoRam[address & 0x1FFF];
				
			case 0xA000:
			case 0xB000:
				return extRam[address & 0x1FFF];
				
			case 0xC000:
			case 0xD000:
			case 0xE000:
				return workRam[address & 0x1FFF];
			
			case 0xF000:
				if (address < 0xFE00) { return workRam[address & 0x1FFF]; }
				else if (address >= 0xFE00 && address < 0xFEA0) { return oam[address & 0x9F]; }
				else if (address >= 0xFF80 && address < 0xFFFF) { return zeroPage[address & 0x7F]; }

				else if (address == 0xFF00) { return joyPadRegister; }
				
				// GPU hookups
				else if (address == 0xFF40) { return gpu.getLCDC(); }
				else if (address == 0xFF41) { return gpu.getSTAT(); }
				else if (address == 0xFF42) { return gpu.getSCY(); }
				else if (address == 0xFF43) { return gpu.getSCX(); }
				else if (address == 0xFF44) { return gpu.getLY(); }

				// Interrupt Flag/Enable
				else if (address == 0xFFFF) { return interruptEnable; }
				else if (address == 0xFF0F) { return interruptFlag; }

				// If address = unused range, I/O registers, or interrupt register, return 0 for now.
				return 0;
			
			default:
				throw new IllegalArgumentException("Unhandled memory read at address: " + address);
		}
	}
	
	/**
	 * Reads 2 bytes from memory arrays, little endian.
	 * @param address - Address to start reading bytes from.
	 * @return 2 bytes from memory.
	 */
	public int readWord(int address) {
		return readByte(address) + (readByte(address + 1) << 8);
	}
	
	/**
	 * Handles writing a byte to the correct region in memory.
	 * @param address - Location of where to store value.
	 * @param value - Value to store.
	 */
	public void writeByte(int address, int value) {
		BitUtil.checkIsWord(address);
		BitUtil.checkIsByte(value);
		switch(address & 0xF000)
		{
			case 0x0000:
			case 0x1000:
			case 0x2000:
			case 0x3000:
			case 0x4000:
			case 0x5000:
			case 0x6000:
			case 0x7000:
				break; // we don't write to ROM!
			
			case 0x8000:
			case 0x9000:
				videoRam[address & 0x1FFF] = value;
				break;
				
			case 0xA000:
			case 0xB000:
				extRam[address & 0x1FFF] = value;
				break;
				
			case 0xC000:
			case 0xD000:
			case 0xE000:
				workRam[address & 0x1FFF] = value;
				break;
				
			case 0xF000:
				if (address < 0xFE00) { workRam[address & 0x1FFF] = value; }
				else if (address >= 0xFE00 && address < 0xFEA0) { oam[address & 0x9F] = value; }
				else if (address >= 0xFF80 && address < 0xFFFF) { zeroPage[address & 0x7F] = value; }

				else if (address == 0xFF00) { break; }

				// GPU hookups
				else if (address == 0xFF40) { gpu.setLCDC(value); }
				else if (address == 0xFF41) { gpu.setSTAT(value); }
				else if (address == 0xFF42) { gpu.setSCY(value); }
				else if (address == 0xFF43) { gpu.setSCX(value); }
				else if (address == 0xFF44) { gpu.resetLY(); }
				else if (address == 0xFF47) { gpu.setBGP(value); }

				// Interrupt Flag/Enable
				else if (address == 0xFF0F) { interruptFlag = value; }
				else if (address == 0xFFFF) { interruptEnable = value; }

				// Boot ROM disable
				else if (address == 0xFF50) { bootRomEnabled = false; }

				break;
				
			default:
				throw new IllegalArgumentException("Unhandled memory write at address: " + address);
		}
	}
	
	/**
	 * Writes a word to desired address in memory, little endian.
	 * @param address - Address to start writing bytes to.
	 * @param value - 2 bytes to write.
	 */
	public void writeWord(int address, int value) {
		BitUtil.checkIsWord(value);
		writeByte(address, value & 0xFF); // least significant bit written first
		writeByte(address + 1, value >> 8);
	}
	
	/**
	 * Initial load of ROM into bank arrays.
	 * @param romArray - ROM banks as array.
	 */
	public void loadROM(int[] romArray) {
		romBank0 = Arrays.copyOfRange(romArray, 0x0000, 0x4000); // 0x0000 - 0x3FFF
		romBank1 = Arrays.copyOfRange(romArray, 0x4000, 0x8000); // 0x4000 - 0x7FFF
	}
	
	/**
	 * Initial load of boot ROM into its array.
	 * @param boot - The boot ROM loaded from file.
	 */
	private void loadBootROM(int[] boot) {
		bootRom = Arrays.copyOfRange(boot, 0x00, 0x100);
	}
	
	/**
	 * Toggles whether or not the boot ROM is currently accessible.
	 * @param state - Desired state of the boot ROM (true for enabled, false for disabled)
	 */
	public void toggleBootROM(boolean state) {
		bootRomEnabled = state;
	}
	
	/**
	 * Fills all of the memory region arrays with 0, effectively resetting them.
	 */
	public void clearMemory() {
		Arrays.fill(romBank0, 0);
		Arrays.fill(romBank1, 0);
		Arrays.fill(videoRam, 0);
		Arrays.fill(extRam, 0);
		Arrays.fill(workRam, 0);
		Arrays.fill(oam, 0);
		Arrays.fill(zeroPage, 0);
	}
}
