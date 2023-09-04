package us.kshadow.gbz80emu.constants;

public class MemoryAddresses {

	private MemoryAddresses() {

	}

	public static final int BOOT_ROM_TOGGLE = 0xFF50;

	public static final int CGB_SPEED_SWITCH = 0xFF4D;

	public static final int INTERRUPT_FLAG = 0xFF0F;

	public static final int INTERRUPT_ENABLE = 0xFFFF;

	public static final int JOY_PAD_REGISTER = 0xFF00;

	// Timer registers
	public static final int TIMER_DIV_REGISTER = 0xFF04;

	public static final int TIMER_TIMA_REGISTER = 0xFF05;

	public static final int TIMER_TMA_REGISTER = 0xFF06;

	public static final int TIMER_TAC_REGISTER = 0xFF07;

	// GPU registers
	public static final int LCD_CONTROL = 0xFF40;

	public static final int LCD_STATUS = 0xFF41;

	public static final int SCROLL_Y = 0xFF42;

	public static final int SCROLL_X = 0xFF43;

	public static final int LINE_Y = 0xFF44;

	public static final int BG_PALETTE = 0xFF47;
}
