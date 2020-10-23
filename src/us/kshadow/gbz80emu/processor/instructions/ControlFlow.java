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
	
	// Handles absolute jump function.
	public static void instructJP(int address) {
		cpuReg.writeReg("PC", address, false);
	}
	
	// Handles conditional jumps.
	public static void instructCondJP(int opcode, int address) {
		switch(opcode) {
		case 0xC2:
			if (!fr.isZ()) {
				instructJP(address);
			}
			break;
		case 0xCA:
			if (fr.isZ()) {
				instructJP(address);
			}
			break;
		case 0xD2:
			if (!fr.isC()) {
				instructJP(address);
			}
			break;
		case 0xDA:
			if (fr.isC()) {
				instructJP(address);
			}
			break;
		default:
			break;
		}
	}
}
