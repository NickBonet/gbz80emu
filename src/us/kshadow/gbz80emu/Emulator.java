package us.kshadow.gbz80emu;

import java.awt.Graphics;
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
	private transient ROMParser testROM = new ROMParser();
	private boolean emuRunning;
	
	public Emulator() {
		emuRunning = true;
		cpu = new CPU();
		try {
			testROM.loadROM("tetris.gb");		
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
					cpu.getRegisters().print();
					gpu.nextStep(cycles);
				}
				cpu.resetCyclesAfterFrame();
			}
			repaint();
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
		drawTileMapFromVRAM(g);
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
