package us.kshadow.gbz80emu.memory;

/**
 * A basic memory management unit abstraction.
 * @author Nicholas Bonet
 */

public class MMU {
	
	private static final MMU instance = new MMU();
	
	
	
	private MMU() { }
	
	public static MMU getInstance() {
		return instance;
	}

}
