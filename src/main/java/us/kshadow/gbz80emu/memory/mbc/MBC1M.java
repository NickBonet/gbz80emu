package us.kshadow.gbz80emu.memory.mbc;

/**
 * Multi-cart MBC1 implementation, mostly uses {@link MBC1} functionality. Only
 * ROM read functionality was modified for MBC1M support.
 */
public class MBC1M extends MBC1 {

	@Override
	public int handleMBCReadROM(int address) {
		int currentBank;

		switch (address & 0xF000) {
			case 0x0000, 0x1000, 0x2000, 0x3000 -> {
				if (mbc1Mode == 1) {
					currentBank = (bankIndex2 << 4);
					return cartridge.getROM()[(address & 0x3FFF) + (0x4000 * currentBank)];
				}
			}

			case 0x4000, 0x5000, 0x6000, 0x7000 -> {
				currentBank = (bankIndex2 << 4) | (bankIndex1 & 0xF);

				if (bankIndex1 == 0x00 || bankIndex1 == 0x20 || bankIndex1 == 0x40 || bankIndex1 == 0x60) {
					return cartridge.getROM()[(address & 0x3FFF) + (0x4000 * (1 + currentBank))];
				}

				return cartridge.getROM()[(address & 0x3FFF) + (0x4000 * currentBank)];
			}

			default -> throw new IllegalArgumentException("Unhandled MBC ROM read at address: " + address);
		}

		return cartridge.getROM()[address];
	}
}
