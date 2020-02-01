package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import us.kshadow.gbz80emu.util.BitUtil;

/**
 * Instructions relating to bit shifts/rotations. (Mostly used in CB prefix instructions.)
 * @author Nicholas Bonet
 */

public class BitShift {

	private static CPURegisters cpuReg = CPURegisters.getInstance();
	private static FlagRegister FR = cpuReg.getFR();
	
	// For swapping upper/lower halves in 8-bit registers.
	public static void instruct_SWAP(String register) {
		int result = ((cpuReg.getReg(register) & 0x0F) << 4 | (cpuReg.getReg(register) & 0xF0) >> 4);
		FR.setZ(result == 0);
		FR.setC(false);
		FR.setN(false);
		FR.setH(false);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate left through carry flag
	public static void instruct_RL(String register) {
		int result = ((cpuReg.getReg(register) << 1) & 0xFF);
		result |= FR.isC() ? 1 : 0;
		FR.setC(BitUtil.checkBitSet(cpuReg.getReg(register), 7));
		FR.setH(false);
		FR.setN(false);
		FR.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	
	// Rotate A register left through carry flag
	public static void instruct_RLA() {
		int result = ((cpuReg.getReg("A") << 1) & 0xFF);
		result |= FR.isC() ? 1 : 0;
		FR.setC(BitUtil.checkBitSet(cpuReg.getReg("A"), 7));
		FR.setH(false);
		FR.setN(false);
		FR.setZ(false);
		cpuReg.writeReg("A", result, false); 
	}
}
