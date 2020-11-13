package us.kshadow.gbz80emu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

@SuppressWarnings("serial")
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
		emu.setBackground(Color.white);
		emu.setPreferredSize(new Dimension(Emulator.WINDOW_WIDTH, Emulator.WINDOW_HEIGHT));
		frame.add(emu);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		emuRunnable = () -> emu.runEmulator();
		Thread emuThread = new Thread(emuRunnable);
		emuThread.start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_P: // pause/resume emulation
			if (emu.getEmuRunning()) {
				emu.setEmuRunning(false);
			} else {
				emu.setEmuRunning(true);
				Thread emuThread = new Thread(emuRunnable);
				emuThread.start();
			}
			break;
		case KeyEvent.VK_N: // next step
			if (!emu.getEmuRunning()) {
				emu.nextStep();
			}
			break;
		case KeyEvent.VK_PAGE_DOWN: // save tile set to file
			emu.dumpTileSetFromVRAM();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}