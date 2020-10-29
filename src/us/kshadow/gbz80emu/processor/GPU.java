package us.kshadow.gbz80emu.processor;

/**
 * GPU - An emulation of the graphical operations the GameBoy performs to draw to its LCD.
 * @author Nicholas Bonet
 *
 */
public class GPU {
	private static final GPU instance = new GPU();
	private static int LCDC; // 0xFF40 - LCD/GPU control
	private static int SCY; // 0xFF42
	private static int SCX; // 0xFF43
	private static int LY = 0x89; // 0xFF44
	
	public static GPU getInstance() {
		return instance;
	}
	
	public int getLY() {
		return LY;
	}
}
