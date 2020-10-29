/**
 * Emulator - edit me!
 * @author Nicholas Bonet
 *
 */
package us.kshadow.gbz80emu;

import java.awt.Graphics;
import java.io.IOException;
import javax.swing.JPanel;

import us.kshadow.gbz80emu.memory.ROMParser;
import us.kshadow.gbz80emu.processor.CPU;

/**
 * Emulator - Where all the moving parts are tied together to load GB games.
 */
@SuppressWarnings("serial")
public class Emulator extends JPanel {
	private final transient CPU cpu;
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
					cpu.nextInstruction();
					cpu.getRegisters().print();
				}
				cpu.resetCyclesAfterFrame();
			}
			repaint();
		}
	}
	
	public void nextStep() {
		cpu.nextInstruction();
		cpu.getRegisters().print();
		if (cpu.getCycles() >= 70224) { 
			cpu.resetCyclesAfterFrame();
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
