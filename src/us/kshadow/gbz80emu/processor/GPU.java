package us.kshadow.gbz80emu.processor;

import java.awt.image.BufferedImage;

import us.kshadow.gbz80emu.memory.MMU;

/**
 * GPU - An emulation of the graphical operations the GameBoy performs to draw to its LCD.
 * @author Nicholas Bonet
 *
 */
public class GPU {
	private static final GPU instance = new GPU();
	private static int LCDC; // 0xFF40 - LCD/GPU control
	private static int STAT; // 0xFF41 - LCDC Status
	private static int SCY; // 0xFF42
	private static int SCX; // 0xFF43
	private static int LY = 0x89; // 0xFF44
	
	public static GPU getInstance() {
		return instance;
	}
	
	public int getLY() {
		return LY;
	}
	
	public BufferedImage readTile(int address) {
		int[] bytes = new int[16];
		BufferedImage tile = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
		// loop through every 2 bytes
		for (int i = 0; i < bytes.length; i += 2) {
			bytes[i] = MMU.getInstance().readByte(address + i);
			for (int j = 0; j < 8; j++) {
				int bitMask = 1 << (7 - j);
				int lsb = bytes[i] & bitMask;
				int msb = bytes[i+1] & bitMask;
				int colorValue = msb << 1 | lsb;
				switch (colorValue) {
				case 0:
					tile.setRGB(j, i/2, 0x9bbc0f);
					break;
				case 1:
					tile.setRGB(j, i/2, 0x8bac0f);
					break;
				case 2:
					tile.setRGB(j, i/2, 0x306230);
					break;
				case 3:
					tile.setRGB(j, i/2, 0x0f380f);
					break;
				default:
					break;
				}
			}
		}
		return tile;
	}
}
