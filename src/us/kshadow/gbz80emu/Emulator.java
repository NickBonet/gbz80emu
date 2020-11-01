package us.kshadow.gbz80emu;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import us.kshadow.gbz80emu.memory.ROMParser;
import us.kshadow.gbz80emu.processor.CPU;
import us.kshadow.gbz80emu.graphics.GPU;
import us.kshadow.gbz80emu.util.MiscUtil;

/**
 * Emulator - Where all the moving parts are tied together to load GB games.
 */
@SuppressWarnings("serial")
public class Emulator extends JPanel {
	public static final int WINDOW_WIDTH = 480;
	public static final int WINDOW_HEIGHT = 432;
	private final transient CPU cpu;
	private static final GPU gpu = GPU.getInstance();
	private final transient ROMParser testROM = new ROMParser();
	private transient final BufferedImage gbDisplay;
	private boolean emuRunning;

	/**
	 * Initializer for the Emulator panel.
	 */
	public Emulator() {
		gbDisplay = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
		emuRunning = true;
		cpu = new CPU();
		try {
			testROM.loadROM("07-jr,jp,call,ret,rst.gb");
			cpu.getMMU().loadROM(testROM.getROMAsArray());
			cpu.getMMU().toggleBootROM(false);
			cpu.getRegisters().setInitValues();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles running the normal emulation loop.
	 */
	@SuppressWarnings("java:S3776")
	public void runEmulator() {
		while (emuRunning) {
			if(cpu.isRunning()) { // for STOP instruction
				while (cpu.getCycles() <= 70224) {
					int cycles = cpu.nextInstruction();
					if (cycles == 0) break;
					gpu.nextStep(cycles);
					cycles = cpu.handleInterrupt();
					gpu.addCycles(cycles);
				}
				if (cpu.getCycles() >= 70224) {
					cpu.resetCyclesAfterFrame();
					repaint();
				}

				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * Allows for a single step of the system to be executed. (CPU/GPU)
	 */
	public void nextStep() {
		int cycles = cpu.nextInstruction();
		cpu.getRegisters().print();
		gpu.nextStep(cycles);
		if (cpu.getCycles() >= 70224) { 
			cpu.resetCyclesAfterFrame();
			repaint();
		}
	}

	/**
	 * Renders the next frame for the emulator view, and resizes it to
	 * the defined width/height.
	 * @return The resized BufferedImage.
	 */
	public BufferedImage renderFrame() {
		for (int x = 0; x < 160; x++) {
			for (int y = 0; y < 144; y++) {
				gbDisplay.setRGB(x, y, gpu.getFramebuffer()[x][y]);
			}
		}
		return MiscUtil.resizeBufferedImage(gbDisplay, WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	/**
	 * Method for dumping the current tile set in VRAM to file.
	 */
	public void dumpTileSetFromVRAM() {
		int x = 0;
		int y = 0;
		BufferedImage fullTileSet = new BufferedImage(256, 384, BufferedImage.TYPE_INT_RGB);
		Graphics g = fullTileSet.getGraphics();
		for (int i = 0x8000; i <= 0x97FF; i+= 0x10) {
			BufferedImage tile = gpu.tileToImage(i);
			if (x == 256) {
				x = 0;
				y += 0x10;
			}
			g.drawImage(MiscUtil.resizeBufferedImage(tile, 16, 16), x, y, null);
			x += 0x10;
		}
		File tileOutputFile = new File("tileset.png");
		try {
			ImageIO.write(fullTileSet, "png", tileOutputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(renderFrame(), 0, 0, null);
	}

	public void setEmuRunning(boolean emuRunning) {
		this.emuRunning = emuRunning;
	}

	public boolean getEmuRunning() {
		return emuRunning;
	}
}
