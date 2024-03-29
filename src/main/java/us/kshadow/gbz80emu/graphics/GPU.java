package us.kshadow.gbz80emu.graphics;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.util.BitUtil;

import static us.kshadow.gbz80emu.constants.MemoryAddresses.INTERRUPT_ENABLE;
import static us.kshadow.gbz80emu.constants.MemoryAddresses.INTERRUPT_FLAG;

/**
 * GPU - An emulation of the graphical operations the Game Boy performs to draw
 * to its LCD.
 * 
 * @author Nicholas Bonet
 *
 */

@SuppressWarnings("java:S6548")
public class GPU {
	// Lightest green, light green, dark green, darkest green.
	private static final int[] DMG_COLORS = {0xe0f8d0, 0x88c070, 0x346856, 0x081820};
	private static final GPU instance = new GPU();
	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final MMU mmu = MMU.getInstance();
	private final int[] currentPalette;
	private int lcdControl; // 0xFF40 - LCD/GPU control
	private int lcdStatus = 0; // 0xFF41 - LCDC Status
	private int scrollY; // 0xFF42
	private int scrollX; // 0xFF43
	private int lineY; // 0xFF44
	private int bgPalette; // 0xFF47, sets palette colors or BG/windows
	private int gpuMode; // Technically a part of LCDC status, will get to that later.
	private int systemCycles;
	// 2D array tp represent GB display. Will store the color
	// to display in our BufferedImage each frame.
	private final int[][] framebuffer;

	/**
	 * Initializer for the emulated GPU/PPU.
	 */
	private GPU() {
		framebuffer = new int[256][256];
		currentPalette = Arrays.copyOf(DMG_COLORS, 4);
	}

	/**
	 * Draws a horizontal line for the Game Boy display.
	 * 
	 * @param line
	 *            - Line to draw. (0-143)
	 */
	public void renderScanLine(int line) {
		int rowIndex = (line / 8);
		for (int columnIndex = 0; columnIndex < 20; columnIndex++) {
			int elementIndex = (rowIndex * 32) + columnIndex;
			int bgTileMapPointer = BitUtil.checkBitSet(lcdControl, 3) ? 0x9C00 : 0x9800;
			int tileIndex = mmu.readByte(bgTileMapPointer + elementIndex);
			int relativeLine = (line % 8);
			int address = BitUtil.checkBitSet(lcdControl, 4)
					? 0x8000 + (tileIndex * 0x10)
					: 0x9000 + (((byte) tileIndex) * 0x10);
			drawTileToFramebuffer(framebuffer, address, columnIndex, rowIndex, relativeLine, relativeLine + 1, scrollX,
					scrollY);
		}
	}

	/**
	 * As the name states, draws a tile onto the framebuffer.
	 * 
	 * @param framebuffer
	 *            - Framebuffer to write pixel data to.
	 * @param address
	 *            - Address of the tile to draw.
	 * @param columnIndex
	 *            - Column index of the tile in relation to the tile map.
	 * @param rowIndex
	 *            - Row index of the tile in relation to the tile map.
	 * @param startAtLine
	 *            - Line of tile to start drawing at.
	 * @param endBeforeLine
	 *            - Line of tile to end drawing before.
	 * @param scrollX
	 *            - SCX offset (if needed).
	 * @param scrollY
	 *            - SCY offset (if needed).
	 */
	@SuppressWarnings("java:S107")
	private void drawTileToFramebuffer(int[][] framebuffer, int address, int columnIndex, int rowIndex, int startAtLine,
			int endBeforeLine, int scrollX, int scrollY) {
		int[] bytes = new int[16];
		// Loop through every 2 bytes (2 bytes = 1 row of tile).
		for (int row = startAtLine * 2; row < endBeforeLine * 2; row += 2) {
			bytes[row] = mmu.readByte(address + row);
			bytes[row + 1] = mmu.readByte(address + row + 1);
			// Loop through bits of each byte to get color information.
			for (int column = 0; column < 8; column++) {
				int lsb = ((bytes[row] >> (7 - column)) & 1);
				int msb = ((bytes[row + 1] >> (7 - column)) & 1);
				int colorValue = msb << 1 | lsb;
				int x = ((column + (8 * columnIndex)) - scrollX) & 0xFF;
				int y = (((row / 2) + (8 * rowIndex)) - scrollY) & 0xFF;
				switch (colorValue) {
					case 0 -> framebuffer[x][y] = currentPalette[0];
					case 1 -> framebuffer[x][y] = currentPalette[1];
					case 2 -> framebuffer[x][y] = currentPalette[2];
					case 3 -> framebuffer[x][y] = currentPalette[3];
					default -> {
					}
				}
			}
		}
	}

	/**
	 * Similar to CPU's nextInstruction(), except for GPU operations.
	 * 
	 * @param cycles
	 *            - CPU cycles to add to internal GPU cycle count.
	 */
	@SuppressWarnings("java:S3776")
	public void nextStep(int cycles) {
		systemCycles += cycles;
		switch (gpuMode) {
			case 0 -> { // HBlank mode
				if (systemCycles >= 204) {
					lineY++;
					if (lineY > 143) {
						int interruptEnable = mmu.readByte(INTERRUPT_ENABLE);
						if (BitUtil.checkBitSet(interruptEnable, 0)) {
							int interruptFlag = mmu.readByte(INTERRUPT_FLAG);
							interruptFlag = BitUtil.setBit(interruptFlag, 0);
							mmu.writeByte(INTERRUPT_FLAG, interruptFlag);
						}
						setGpuMode(1);
					} else {
						setGpuMode(2);
					}
					systemCycles -= 204;
				}
			}
			case 1 -> { // VBlank mode
				if (systemCycles >= 456) {
					lineY++;
					if (lineY > 153) {
						setGpuMode(2);
						lineY = 0;
					}
					systemCycles -= 456;
				}
			}
			case 2 -> { // Searching OAM
				if (systemCycles >= 80) {
					// TODO: should probably render sprites yeah?
					setGpuMode(3);
					systemCycles -= 80;
				}
			}
			case 3 -> { // Transfer data to display
				if (systemCycles >= 172) {
					setGpuMode(0);
					// render scanline here
					renderScanLine(lineY);
					systemCycles -= 172;
				}
			}
			default -> {
			}
		}
	}

	/**
	 * Reads a tile at a given address in VRAM, and renders it as a BufferedImage.
	 * 
	 * @param address
	 *            - The address to read the tile data from.
	 * @return The tile as a BufferedImage.
	 */
	public BufferedImage tileToImage(int address) {
		int[][] tileFramebuffer = new int[8][8];
		drawTileToFramebuffer(tileFramebuffer, address, 0, 0, 0, 8, 0, 0);
		BufferedImage tile = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++)
				tile.setRGB(x, y, tileFramebuffer[x][y]);
		}
		return tile;
	}

	public static GPU getInstance() {
		return instance;
	}

	public int[][] getFramebuffer() {
		return framebuffer;
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

	public void setSCX(int scrollX) {
		this.scrollX = scrollX;
	}

	public int getSTAT() {
		return lcdStatus;
	}

	public void setSTAT(int lcdStatus) {
		this.lcdStatus = lcdStatus;
	}

	public int getBGP() {
		return bgPalette;
	}

	/**
	 * Updates color palette assignments on changes to BGP register.
	 */
	public void setBGP(int bgPalette) {
		currentPalette[3] = DMG_COLORS[(bgPalette & 0xC0) >> 6];
		currentPalette[2] = DMG_COLORS[(bgPalette & 0x30) >> 4];
		currentPalette[1] = DMG_COLORS[(bgPalette & 0xC) >> 2];
		currentPalette[0] = DMG_COLORS[bgPalette & 0x3];
		this.bgPalette = bgPalette;
	}

	public void setGpuMode(int gpuMode) {
		this.gpuMode = gpuMode;
		switch (gpuMode) {
			case 0 -> {
				lcdStatus &= ~(1 << 1);
				lcdStatus &= ~1;
			}
			case 1 -> {
				lcdStatus &= ~(1 << 1);
				lcdStatus |= 1;
			}
			case 2 -> {
				lcdStatus |= (1 << 1);
				lcdStatus &= ~1;
			}
			case 3 -> {
				lcdStatus |= (1 << 1);
				lcdStatus |= 1;
			}
			default -> {
			}
		}
	}

	public void addCycles(int cycles) {
		systemCycles += cycles;
	}

}
