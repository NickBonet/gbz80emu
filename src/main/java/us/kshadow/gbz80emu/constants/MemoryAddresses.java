package us.kshadow.gbz80emu.constants;

public class MemoryAddresses {

	private MemoryAddresses() {

	}

	public static final int INTERRUPT_FLAG = 0xFF0F;

	public static final int INTERRUPT_ENABLE = 0xFFFF;

	public static final int JOY_PAD_REGISTER = 0xFF00;

	public static final int TIMER_DIV_REGISTER = 0xFF04;

	public static final int TIMER_TIMA_REGISTER = 0xFF05;

	public static final int TIMER_TMA_REGISTER = 0xFF06;

	public static final int TIMER_TAC_REGISTER = 0xFF07;
}
