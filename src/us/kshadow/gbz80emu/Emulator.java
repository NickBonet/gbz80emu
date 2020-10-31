package us.kshadow.gbz80emu;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JPanel;
import us.kshadow.gbz80emu.memory.ROMParser;
import us.kshadow.gbz80emu.processor.CPU;
import us.kshadow.gbz80emu.processor.GPU;

/**
 * Emulator - Where all the moving parts are tied together to load GB games.
 */
@SuppressWarnings("serial")
public class Emulator extends JPanel {
	private final transient CPU cpu;
	private static final GPU gpu = GPU.getInstance();
	private final transient ROMParser testROM = new ROMParser();
	private boolean emuRunning;
	
	public Emulator() {
		emuRunning = true;
		cpu = new CPU();
		try {
			testROM.loadROM("03-op sp,hl.gb");
			cpu.getMMU().loadROM(testROM.getROMAsArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runEmulator() {
		while (emuRunning) {
			if(cpu.isRunning()) { // for STOP instruction
				while (cpu.getCycles() <= 70224) {
					int cycles = cpu.nextInstruction();
					if (cycles == 0) break;
					gpu.nextStep(cycles);
				}
				if (cpu.getCycles() >= 70224) { cpu.resetCyclesAfterFrame(); }
				repaint();
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	public void nextStep() {
		int cycles = cpu.nextInstruction();
		cpu.getRegisters().print();
		gpu.nextStep(cycles);
		if (cpu.getCycles() >= 70224) { 
			cpu.resetCyclesAfterFrame();
		}
	}
	
	public void dumpTile(int address) {
		gpu.saveTile(address, "tile");
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(renderFrame(), 0, 0, null);
		//drawTileMapFromVRAM(g);
	}

	public BufferedImage renderFrame() {
		BufferedImage image = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < 160; x++) {
			for (int y = 0; y < 144; y++) {
				image.setRGB(x, y, gpu.framebuffer[x][y]);
			}
		}
		Image tmp = image.getScaledInstance(480, 432, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(488, 432, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return dimg;
	}

	public void setEmuRunning(boolean emuRunning) {
		this.emuRunning = emuRunning;
	}
	
	public boolean getEmuRunning() {
		return emuRunning;
	}

	/**
	 * Method for drawing tile map to window display.
	 * @param g - Graphics object to draw to.
	 */
	private void drawTileMapFromVRAM(Graphics g) {
		int x = 0;
		int y = 0;
		for (int i = 0x8000; i <= 0x97FF; i+= 0x10) {
			if (x == 304) {
				x = 0;
				y += 0x10;
			}
			g.drawImage(gpu.resizeTile(i), x, y, null);
			x += 0x10;
		}
	}
}
