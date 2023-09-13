package us.kshadow.gbz80emu.memory.mbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kshadow.gbz80emu.memory.Cartridge;

public class MBC1 implements MBC {

	private static final Logger logger = LoggerFactory.getLogger(MBC1.class);

	private static final Cartridge cartridge = Cartridge.getInstance();

	// 0xA000 - 0xBFFF - External Cart RAM
	// Sized up array for extra RAM banks. May refactor.
	private final int[] extRam = new int[0x8000];

	private boolean extRamEnabled;

	private int bankIndex1 = 1;

	private int bankIndex2 = 0;

	private int mbc1Mode = 0;

	@Override
	public int handleMBCReadROM(int address) {
		int currentBank;
		int correctedBankMask = (int) Math.pow(2, cartridge.getROMSize() + 1.0) - 1;

		switch (address & 0xF000) {
			case 0x0000, 0x1000, 0x2000, 0x3000 -> {
				if (mbc1Mode == 1) {
					currentBank = (bankIndex2 << 5);
					return cartridge.getROM()[(address & 0x3FFF) + (0x4000 * (currentBank & correctedBankMask))];
				}
			}

			case 0x4000, 0x5000, 0x6000, 0x7000 -> {
				currentBank = cartridge.getROMSize() >= 5 ? (bankIndex2 << 5) | bankIndex1 : bankIndex1;

				if (currentBank == 0x00 || currentBank == 0x20 || currentBank == 0x40 || currentBank == 0x60) {
					currentBank++;
				}

				return cartridge.getROM()[(address & 0x3FFF) + (0x4000 * (currentBank & correctedBankMask))];
			}

			default -> throw new IllegalArgumentException("Unhandled MBC ROM read at address: " + address);
		}

		return cartridge.getROM()[address];
	}

	@Override
	public void handleMBCWriteROM(int address, int value) {
		if (address >= 0x0000 && address <= 0x1FFF) {
			logger.debug("MBC RAM Enable: {}", value);
			extRamEnabled = ((value & 0xF) == 0xA);
		}

		if (address >= 0x2000 && address <= 0x3FFF) {
			bankIndex1 = value & 0x1F;
		}

		if (address >= 0x4000 && address <= 0x5FFF) {
			bankIndex2 = value & 0x3;
			logger.debug("MBC RAM Bank: {}", bankIndex2);
		}

		if (address >= 0x6000 && address <= 0x7FFF) {
			logger.debug("MBC Mode: {}", value);
			mbc1Mode = value;
		}
	}

	@Override
	public int handleMBCReadRAM(int address) {
		if (extRamEnabled) {
			if (mbc1Mode == 1 && cartridge.getRAMSize() > 2) {
				int translatedAddress = address & 0x1FFF;
				return extRam[translatedAddress + (0x2000 * bankIndex2)];
			} else {
				return extRam[address & 0x1FFF];
			}
		}

		return 0xFF;
	}

	@Override
	public void handleMBCWriteRAM(int address, int value) {
		if (extRamEnabled) {
			if (mbc1Mode == 1 && cartridge.getRAMSize() > 2) {
				int translatedAddress = address & 0x1FFF;
				extRam[translatedAddress + (0x2000 * bankIndex2)] = value;
			} else {
				extRam[address & 0x1FFF] = value;
			}
		}
	}
}
