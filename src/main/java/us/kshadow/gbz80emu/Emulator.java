package us.kshadow.gbz80emu;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kshadow.gbz80emu.memory.Cartridge;
import us.kshadow.gbz80emu.memory.mbc.MBC1;
import us.kshadow.gbz80emu.processor.CPU;
import us.kshadow.gbz80emu.graphics.GPU;
import us.kshadow.gbz80emu.sysclock.SystemTimer;
import us.kshadow.gbz80emu.util.MiscUtil;

/**
 * Emulator - Where all the moving parts are tied together to load GB games.
 */
public class Emulator extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(Emulator.class);
	public static final int WINDOW_WIDTH = 480;
	public static final int WINDOW_HEIGHT = 432;
	private final transient CPU cpu;
	private static final GPU gpu = GPU.getInstance();
	private static final SystemTimer timer = SystemTimer.getInstance();
	private final transient Cartridge testROM = Cartridge.getInstance();
	private final transient BufferedImage gbDisplay;
	private boolean emuRunning;
	private String currentRomFile = "test_roms/cpu_instrs.gb";

	/**
	 * Initializer for the Emulator panel.
	 */
	public Emulator() {
		gbDisplay = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
		emuRunning = true;
		cpu = new CPU();
		setupEmuROM(currentRomFile);
	}

	private void setupEmuROM(String currentRomFile) {
		try {
			testROM.loadROM(currentRomFile);
			logger.info("ROM loaded! | MBC type: {} | ROM size: {} | RAM size: {}", testROM.getMBCType(),
					testROM.getROMSize(), testROM.getRAMSize());

			if (testROM.getMBCType() >= 1 && testROM.getMBCType() <= 3) {
				cpu.getMMU().setMBC(new MBC1());
			} else {
				// Unimplemented MBC
				cpu.getMMU().setMBC(null);
			}

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
			if (cpu.isRunning()) { // for STOP instruction
				while (cpu.getCycles() <= 70224) {
					nextSystemStep();
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
	 * Allows for a single step of the system to be executed. Intended for debug
	 * (CPU/GPU)
	 */
	public void nextDebugStep() {
		nextInstructionStep();
		cpu.getRegisters().print();
		nextInterruptStep();

		if (cpu.getCycles() >= 70224) {
			cpu.resetCyclesAfterFrame();
			repaint();
		}
	}

	/**
	 * Renders the next frame for the emulator view, and resizes it to the defined
	 * width/height.
	 * 
	 * @return The resized BufferedImage.
	 */
	// TODO: Remove setRGB, and make this more efficient.
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
		for (int i = 0x8000; i <= 0x97FF; i += 0x10) {
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

	public void setCurrentRomFile(String currentRomFile) {
		this.currentRomFile = currentRomFile;
		setupEmuROM(currentRomFile);
	}

	/**
	 * Run next CPU instruction and subsystems steps as well.
	 */
	private void nextInstructionStep() {
		int cycles = cpu.nextInstruction();
		timer.handleTimerTick(cycles);
		gpu.nextStep(cycles);
	}

	/**
	 * Handle interrupts and subsystem steps as well.
	 */
	private void nextInterruptStep() {
		int cycles = cpu.handleInterrupt();
		timer.handleTimerTick(cycles);
		gpu.addCycles(cycles);
	}

	/**
	 * Next step in the system as a whole, both instruction processing & interrupt
	 * handling. Can add onto this as needed.
	 */
	private void nextSystemStep() {
		nextInstructionStep();
		nextInterruptStep();
	}
}
