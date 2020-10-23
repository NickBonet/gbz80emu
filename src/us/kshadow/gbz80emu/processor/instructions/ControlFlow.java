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
		cpuReg.writeReg("PC", address, true);
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
	
	// Handles relative jump, based on next byte in memory (signed).
	public static void instructJR(byte value) {
		cpuReg.writeReg("PC", cpuReg.getReg("PC") + value, false);
	}
	
	public static void instructCondJR(int opcode, byte value) {
		switch(opcode) {
		case 0x20:
			if (!fr.isZ()) {
				instructJR(value);
			}
			break;
		case 0x28:
			if (fr.isZ()) {
				instructJR(value);
			}
			break;
		case 0x30:
			if (!fr.isC()) {
				instructJR(value);
			}
			break;
		case 0x38:
			if (fr.isC()) {
				instructJR(value);
			}
			break;
		default:
			break;
		}
	}
}
