package us.kshadow.gbz80emu.processor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
		// loop through every 2 bytes (2 bytes = 1 row of tile)
		for (int i = 0; i < bytes.length; i += 2) {
			bytes[i] = MMU.getInstance().readByte(address + i);
			bytes[i + 1] = MMU.getInstance().readByte(address + i + 1);
			// loop through bits of each byte to get color information.
			for (int j = 0; j < 8; j++) {
				int lsb = ((bytes[i] >> 7 - j) & 1);
				int msb = ((bytes[i+1] >> 7 - j) & 1);
				int colorValue = msb << 1 | lsb;
				switch (colorValue) {
				case 0:
					tile.setRGB(j, i/2, 0xe0f8d0);
					break;
				case 1:
					tile.setRGB(j, i/2, 0x88c070);
					break;
				case 2:
					tile.setRGB(j, i/2, 0x346856);
					break;
				case 3:
					tile.setRGB(j, i/2, 0x081820);
					break;
				default:
					break;
				}
			}
		}
		return tile;
	}
	
	public void saveTile(int address, String name) {
		BufferedImage dimg = resizeTile(address);
		File output = new File(name + ".png");
		try {
			ImageIO.write(dimg, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Function I use for resizing tiles while testing.
	public BufferedImage resizeTile(int address) {
		BufferedImage tile = readTile(address);
		Image tmp = tile.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return dimg;
	}
}
