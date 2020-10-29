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
		for (int i = 0; i <= 0x190; i+=0x10) {
			g.drawImage(gpu.readTile(0x8000+i), i/2, 0, null);
		}
	}
	
	public void setEmuRunning(boolean emuRunning) {
		this.emuRunning = emuRunning;
	}
	
	public boolean getEmuRunning() {
		return emuRunning;
	}
}
