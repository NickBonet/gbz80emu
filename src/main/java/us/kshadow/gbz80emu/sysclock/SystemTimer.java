package us.kshadow.gbz80emu.sysclock;

import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.util.BitUtil;

import static us.kshadow.gbz80emu.constants.MemoryAddresses.INTERRUPT_FLAG;
import static us.kshadow.gbz80emu.constants.MemoryAddresses.TIMER_DIV_REGISTER;
import static us.kshadow.gbz80emu.constants.MemoryAddresses.TIMER_TAC_REGISTER;
import static us.kshadow.gbz80emu.constants.MemoryAddresses.TIMER_TIMA_REGISTER;
import static us.kshadow.gbz80emu.constants.MemoryAddresses.TIMER_TMA_REGISTER;
import static us.kshadow.gbz80emu.util.BitUtil.checkBitSet;

/**
 * Provides emulation of the Game Boy's system clock, which are accessed via the
 * DIV and TIMA registers.
 */
@SuppressWarnings("java:S6548")
public class SystemTimer {

	private static final SystemTimer instance = new SystemTimer();

	private static final MMU mmu = MMU.getInstance();

	// Timer that runs only if TAC is set to enable.
	private int timaRegister;

	// Timer that always runs every 256 t-cycles.
	private int divRegister;

	// Fallback value for TIMA upon overflow.
	private int tmaRegister;

	// Controls the TIMA timer, and what speed it increments at in terms of
	// t-cycles.
	private int tacRegister;

	// Provides a means of tracking cycles for the purpose of DIV register
	// increments.
	private int divCycleCounter;

	// Provides a mean of tracking cycles for the purpose of TIMA register
	// increments.
	private int timaCycleCounter;

	private SystemTimer() {
		timaRegister = 0;
		tmaRegister = 0;
		divRegister = 0;
		tacRegister = 0;
	}

	/**
	 * Wrapper function for writing to system timer registers.
	 * 
	 * @param address
	 *            Memory address of the timer register.
	 * @param value
	 *            Value to write to the applicable register.
	 */
	public void writeSystemTimerRegister(int address, int value) {
		switch (address) {
			case TIMER_DIV_REGISTER -> {
				divRegister = 0;
				divCycleCounter = 0;

				// TIMA's internal cycle counter should be reset on DIV writes
				timaCycleCounter = 0;
			}
			case TIMER_TIMA_REGISTER -> timaRegister = value;
			case TIMER_TMA_REGISTER -> tmaRegister = value;
			case TIMER_TAC_REGISTER -> tacRegister = value;
			default -> throw new IllegalStateException("Unexpected address for timer register write: " + address);
		}
	}

	/**
	 * Wrapper function for reading from system timer registers.
	 * 
	 * @param address
	 *            Memory address of the timer register.
	 * @return requested timer register
	 */
	public int readSystemTimerRegister(int address) {
		switch (address) {
			case TIMER_DIV_REGISTER -> {
				return divRegister;
			}
			case TIMER_TIMA_REGISTER -> {
				return timaRegister;
			}
			case TIMER_TMA_REGISTER -> {
				return tmaRegister;
			}
			case TIMER_TAC_REGISTER -> {
				return tacRegister;
			}
			default -> throw new IllegalStateException("Unexpected address for timer register read: " + address);
		}
	}

	/**
	 * Handles a tick of the system timer, both DIV and TIMA if necessary.
	 * 
	 * @param cycles
	 *            Cycles to add to DIV/TIMA cycle trackers.
	 */
	public void handleTimerTick(int cycles) {
		incrementDIVRegister(cycles);
		timaCycleCounter += cycles;

		// Tick TIMA if TAC allows it.
		if (checkBitSet(tacRegister, 2)) {
			switch (tacRegister & 0x3) {
				case 0x00 -> incrementTIMARegister(1024);
				case 0x01 -> incrementTIMARegister(16);
				case 0x02 -> incrementTIMARegister(64);
				case 0x03 -> incrementTIMARegister(256);
				default -> throw new IllegalStateException("Unexpected TAC value: " + (tacRegister & 0x3));
			}
		}
	}

	private void incrementDIVRegister(int cycles) {
		divCycleCounter += cycles;
		if (divCycleCounter >= 256) {
			divCycleCounter -= 256;
			if (divRegister < 0xFF) {
				divRegister++;
			} else {
				divRegister = 0;
			}
		}
	}

	private void incrementTIMARegister(int frequency) {
		if (timaCycleCounter >= frequency) {
			timaCycleCounter -= frequency;

			if (timaRegister == 0xFF) {
				timaRegister = tmaRegister;
				int interruptFlag = mmu.readByte(INTERRUPT_FLAG);
				if (!checkBitSet(interruptFlag, 2)) {
					mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 2));
				}
			}

			if (timaRegister < 0xFF) {
				timaRegister++;
			}
		}
	}

	public static SystemTimer getInstance() {
		return instance;
	}
}
