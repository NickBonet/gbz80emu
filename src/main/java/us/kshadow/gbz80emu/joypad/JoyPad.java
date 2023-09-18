package us.kshadow.gbz80emu.joypad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kshadow.gbz80emu.memory.MMU;
import us.kshadow.gbz80emu.util.BitUtil;

import static us.kshadow.gbz80emu.constants.MemoryAddresses.INTERRUPT_FLAG;

/**
 * Handle joy pad inputs for Game Boy emulation.
 */
@SuppressWarnings("java:S6548")
public class JoyPad {

	private static final JoyPad instance = new JoyPad();

	private static final MMU mmu = MMU.getInstance();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// Decided to map these into separate values, for clarity’s sake.
	// Depending on the value of controlSelect, actionRegister or directionRegister
	// is returned along with it.
	private int controlSelect = 0xF;
	private int actionRegister = 0xF;
	private int directionRegister = 0xF;

	private JoyPad() {

	}

	public void setJoyPadSelectMode(int value) {
		controlSelect = value;
	}

	public int getJoyPadRegister() {
		// interested in action buttons if bit 5 is 0.
		if (!BitUtil.checkBitSet(controlSelect, 5)) {
			return 0xC0 | controlSelect | actionRegister;
		} else if (!BitUtil.checkBitSet(controlSelect, 4)) {
			return 0xC0 | controlSelect | directionRegister;
		} else {
			return 0xC0 | controlSelect | 0xF;
		}
	}

	public static JoyPad getInstance() {
		return instance;
	}

	@SuppressWarnings("java:S1301")
	public void inputPressed(Input input) {
		logger.debug("{} pressed!", input);

		switch (input) {
			case START, SELECT, A, B -> {
				if (BitUtil.checkBitSet(actionRegister, input.getBit())) {
					actionRegister = BitUtil.setBit(actionRegister, input.getBit());
				}
			}

			case UP, DOWN, LEFT, RIGHT -> {
				if (BitUtil.checkBitSet(directionRegister, input.getBit())) {
					directionRegister = BitUtil.setBit(directionRegister, input.getBit());
				}
			}
		}

		// flag interrupt for action buttons (if necessary)
		if (!BitUtil.checkBitSet(controlSelect, 5) && ((actionRegister & 0xF) != 0xF)) {
			logger.debug("Interrupt on action register occurred.");
			int interruptFlag = mmu.readByte(INTERRUPT_FLAG);
			mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 4));
		}

		// flag interrupt for direction buttons (if necessary)
		if (!BitUtil.checkBitSet(controlSelect, 4) && ((directionRegister & 0xF) != 0xF)) {
			logger.debug("Interrupt on direction register occurred.");
			int interruptFlag = mmu.readByte(INTERRUPT_FLAG);
			mmu.writeByte(INTERRUPT_FLAG, BitUtil.setBit(interruptFlag, 4));
		}
	}

	@SuppressWarnings("java:S1301")
	public void inputReleased(Input input) {
		logger.debug("{} released!", input);

		switch (input) {
			case START, SELECT, A, B -> {
				if (!BitUtil.checkBitSet(actionRegister, input.getBit())) {
					actionRegister = BitUtil.setBit(actionRegister, input.getBit());
				}
			}

			case UP, DOWN, LEFT, RIGHT -> {
				if (!BitUtil.checkBitSet(directionRegister, input.getBit())) {
					directionRegister = BitUtil.setBit(directionRegister, input.getBit());
				}
			}
		}
	}
}
