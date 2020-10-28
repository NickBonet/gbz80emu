/**
 * Emulator - edit me!
 * @author Nicholas Bonet
 *
 */
package us.kshadow.gbz80emu;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.JPanel;

import us.kshadow.gbz80emu.memory.ROMParser;
import us.kshadow.gbz80emu.processor.CPU;

/**
 * Emulator - Where all the moving parts are tied together to load GB games.
 */
@SuppressWarnings("serial")
public class Emulator extends JPanel {
	private final CPU cpu;
	private ROMParser testROM = new ROMParser();
	private final transient Logger logger = Logger.getLogger("GBZ80Emu");
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
				for (; cpu.cycles <= 70224; cpu.cycles++) {
					cpu.fetchInstruction();
					cpu.getRegisters().print();
				}
				cpu.cycles = 0;
			}
			repaint();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void setEmuRunning(boolean emuRunning) {
		this.emuRunning = emuRunning;
	}
	
	public boolean getEmuRunning() {
		return emuRunning;
	}
}
