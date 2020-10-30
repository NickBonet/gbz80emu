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
	//private static int lcdStatus; // 0xFF41 - LCDC Status
	private int lcdControl; // 0xFF40 - LCD/GPU control
	private int scrollY; // 0xFF42
	private int scrollX; // 0xFF43
	private int lineY; // 0xFF44
	private int bgPalette; // 0xFF47
	private int gpuMode; // Technically a part of LCDC status, will get to that later.
	private int systemCycles;

	private GPU() {

	}

	/**
	 * Similar to CPU's nextInstruction(), except for GPU operations.
	 * @param cycles - CPU cycles to add to internal GPU cycle count.
	 */
	public void nextStep(int cycles) {
		systemCycles += cycles;
		switch(gpuMode) {
			case 0: // HBlank mode
				if (systemCycles >= 204) {
					lineY++;
					if (lineY == 143) {
						gpuMode = 1;
					} else {
						gpuMode = 2;
					}
					systemCycles -= 204;
				}
				break;
			case 1: // VBlank mode
				if (systemCycles >= 456) {
					lineY++;
					if(lineY > 153) {
						gpuMode = 2;
						lineY = 0;
					}
					systemCycles -= 456;
				}
				break;
			case 2: // Searching OAM
				if (systemCycles >= 80) {
					gpuMode = 3;
					systemCycles -= 80;
				}
				break;
			case 3: // Transfer data to display
				if (systemCycles >= 172) {
					gpuMode = 0;
					// render scanline here
					systemCycles -= 172;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Reads a tile at a given address in VRAM, and create it as a BufferedImage.
	 * @param address - The address to read the tile data from.
	 * @return tile - The tile as a BufferedImage.
	 */
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

	public static GPU getInstance() {
		return instance;
	}

	public int getLY() {
		return lineY;
	}

	public void resetLY() {
		lineY = 0;
	}

	public int getLCDC() {
		return lcdControl;
	}

	public void setLCDC(int lcdControl) {
		this.lcdControl = lcdControl;
	}

	public int getSCY() {
		return scrollY;
	}

	public void setSCY(int scrollY) {
		this.scrollY = scrollY;
	}

	public int getSCX() {
		return scrollX;
	}

	public void setSCX(int scrollY) {
		this.scrollX = scrollX;
	}

	public int getBGP() {
		return bgPalette;
	}

	public void setBGP(int bgPalette) {
		this.bgPalette = bgPalette;
	}

}
