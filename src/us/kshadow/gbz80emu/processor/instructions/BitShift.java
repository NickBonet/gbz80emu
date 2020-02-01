package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import static us.kshadow.gbz80emu.util.BitUtil.checkBitSet;;

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
		FR.setC(checkBitSet(cpuReg.getReg(register), 7));
		FR.setH(false);
		FR.setN(false);
		FR.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	
	// Rotate A register left through carry flag
	public static void instruct_RLA() {
		int result = ((cpuReg.getReg("A") << 1) & 0xFF);
		result |= FR.isC() ? 1 : 0;
		FR.setC(checkBitSet(cpuReg.getReg("A"), 7));
		FR.setH(false);
		FR.setN(false);
		FR.setZ(false);
		cpuReg.writeReg("A", result, false); 
	}
	
	// Rotate register left. Bit 7 from initial value goes into carry flag.
	public static void instruct_RLC(String register) {
		int result = ((cpuReg.getReg(register) << 1) & 0xFF);
		result |= checkBitSet(cpuReg.getReg(register), 7) ? 1 : 0;
		FR.setC(checkBitSet(cpuReg.getReg(register), 7));
		FR.setN(false);
		FR.setH(false);
		FR.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A left. Bit 7 from initial value goes into carry flag.
	public static void instruct_RLCA() {
		int result = ((cpuReg.getReg("A") << 1) & 0xFF);
		result |= checkBitSet(cpuReg.getReg("A"), 7) ? 1 : 0;
		FR.setC(checkBitSet(cpuReg.getReg("A"), 7));
		FR.setN(false);
		FR.setH(false);
		FR.setZ(false);
		cpuReg.writeReg("A", result, false);
	}
}
