package us.kshadow.gbz80emu.memory.mbc;

/**
 * Interface for memory bank controller classes to implement. Allows for moving
 * ROM banking and external RAM handling out of the MMU, and allows for
 * selectively choosing MBC implementations based on the loaded ROM.
 *
 */
public interface MBC {

	/**
	 * Handle requested reads for ROM based on MBC implementation.
	 *
	 * @param address
	 *            - requested address to read from
	 */
	int handleMBCReadROM(int address);

	/**
	 * Handle writes for ROM memory addresses that affect MBC registers.
	 *
	 * @param address
	 *            - requested write address
	 * @param value
	 *            - value to write
	 */
	void handleMBCWriteROM(int address, int value);

	/**
	 * Handle reads for external RAM based on MBC implementation.
	 *
	 * @param address
	 *            - requested address to read from
	 */
	int handleMBCReadRAM(int address);

	/**
	 * Handle writes to external RAM based on MBC implementation.
	 *
	 * @param address
	 *            - requested write address
	 * @param value
	 *            - value to write
	 */
	void handleMBCWriteRAM(int address, int value);
}
