package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;

/**
 * Contains implementation for instructions pertaining to control flow/branching.
 * @author Nicholas Bonet
 *
 */
public class ControlFlow {

	private static final CPURegisters cpuReg = CPURegisters.getInstance();
	private static final FlagRegister fr = cpuReg.getFR();
	
	private ControlFlow() { }
	
	// Handles conditional jump/absolute jump instruction.
	public static void instructJP() {
		//TODO: Need to finish implementing
	}
}
