package us.kshadow.gbz80emu;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends JFrame implements KeyListener {
	private static final Emulator emu = new Emulator();
	private static Runnable emuRunnable;

	public Main(String title) {
		super(title);
		setFocusable(true);
		addKeyListener(this);
	}

	public static void main(String[] args) {
		JFrame frame = new Main("GBZ80Emu");
		if (args.length > 0)
			emu.setCurrentRomFile(args[0]);
		emu.setBackground(Color.white);
		emu.setPreferredSize(new Dimension(Emulator.WINDOW_WIDTH, Emulator.WINDOW_HEIGHT));
		frame.add(emu);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		emuRunnable = emu::runEmulator;
		Thread emuThread = new Thread(emuRunnable);
		emuThread.start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_P -> { // pause/resume emulation
				if (emu.getEmuRunning()) {
					emu.setEmuRunning(false);
				} else {
					emu.setEmuRunning(true);
					Thread emuThread = new Thread(emuRunnable);
					emuThread.start();
				}
			}
			case KeyEvent.VK_N -> { // next step
				if (!emu.getEmuRunning()) {
					emu.nextStep();
				}
			}
			case KeyEvent.VK_PAGE_DOWN -> // save tile set to file
				emu.dumpTileSetFromVRAM();
			default -> {
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}