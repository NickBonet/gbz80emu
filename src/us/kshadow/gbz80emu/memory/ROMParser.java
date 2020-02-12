package us.kshadow.gbz80emu.memory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ROMParser {

	// Integer array that the ROM file is loaded into.
	private int[] romArray;
	
	// Array of bytes that make up the Nintendo logo.
	private final int[] logoArray = new int[] {
			 0xCE, 0xED, 0x66, 0x66, 0xCC, 0x0D, 0x00, 0x0B, 0x03, 0x73, 0x00, 0x83, 0x00, 0x0C, 0x00, 0x0D,
			 0x00, 0x08, 0x11, 0x1F, 0x88, 0x89, 0x00, 0x0E, 0xDC, 0xCC, 0x6E, 0xE6, 0xDD, 0xDD, 0xD9, 0x99,
			 0xBB, 0xBB, 0x67, 0x63, 0x6E, 0x0E, 0xEC, 0xCC, 0xDD, 0xDC, 0x99, 0x9F, 0xBB, 0xB9, 0x33, 0x3E
	};
	
	public ROMParser(String file) {
		try {
			loadROM(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadROM(String file) throws IOException {
		try (InputStream romStream = new FileInputStream(file)) {
			byte[] romByteArray = new byte[romStream.available()];
			romStream.read(romByteArray);
			romArray = new int[romByteArray.length];
			for (int i = 0; i < romByteArray.length; i++) {
				romArray[i] = romByteArray[i] & 0xFF;
			}
		} catch (IOException ex) { ex.printStackTrace(); }
	}
	
	public int[] getRomAsArray() {
		return romArray;
	}
	
	public String getTitle() {
		char[] title = new char[16];
		for (int i = 0; i < title.length; i++) {
			title[i] = (char) romArray[i + 0x134];
		}
		return String.copyValueOf(title);
	}
	
	public boolean isLogoValid() {
		return Arrays.equals(logoArray, Arrays.copyOfRange(romArray, 0x104, 0x134));
	}
	
	public boolean hasSgbFuncSupport() {
		return (romArray[0x146] == 0x03);
	}
	
	public boolean isHeaderValid() {
		int check = 0;
		for (int i = 0x134; i <= 0x14c; i++) {
			check = (check - romArray[i] - 1) & 0xFF;
		}
		return (check == romArray[0x14D]);
	}
	
	public boolean isCartridgeValid() {
		int check = 0;
		for (int i = 0; i < romArray.length; i++) {
			check = (check + romArray[i]) & 0xFFFF;
		}
		check = check - romArray[0x14E] - romArray[0x14F];
		int sumInCart = (romArray[0x14E] << 8) ^ romArray[0x14F];
		return (sumInCart == check);
	}
}
