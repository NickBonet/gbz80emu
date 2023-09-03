package us.kshadow.gbz80emu.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Any misc. utility methods that don't really fall into scope anywhere else in
 * the project go here.
 * 
 * @author Nicholas Bonet
 */
public class MiscUtil {

	private MiscUtil() {
	}

	/**
	 * Handles resizing a BufferedImage, mostly used for upscaling the frames a bit
	 * in the emulator.
	 * 
	 * @param image
	 *            - The image to upscale.
	 * @param width
	 *            - Desired width of resized image.
	 * @param height
	 *            - Desired height of resized image.
	 * @return The resized image.
	 */
	public static BufferedImage resizeBufferedImage(BufferedImage image, int width, int height) {
		Image tempImg = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = resizedImg.createGraphics();
		g.drawImage(tempImg, 0, 0, null);
		g.dispose();
		return resizedImg;
	}

}
