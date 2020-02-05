package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import static us.kshadow.gbz80emu.util.BitUtil.checkBitSet;
import static us.kshadow.gbz80emu.util.BitUtil.setBit;

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
		result |= FR.isC() ? 1 : 0; // puts carry bit into bit 0 if set
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
	
	// Rotate register left. Bit 7 from initial value goes into carry flag and bit 0.
	public static void instruct_RLC(String register) {
		int result = ((cpuReg.getReg(register) << 1) & 0xFF);
		result |= checkBitSet(cpuReg.getReg(register), 7) ? 1 : 0;
		FR.setC(checkBitSet(cpuReg.getReg(register), 7));
		FR.setN(false);
		FR.setH(false);
		FR.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A left. Bit 7 from initial value goes into carry flag and bit 0.
	public static void instruct_RLCA() {
		int result = ((cpuReg.getReg("A") << 1) & 0xFF);
		result |= checkBitSet(cpuReg.getReg("A"), 7) ? 1 : 0;
		FR.setC(checkBitSet(cpuReg.getReg("A"), 7));
		FR.setN(false);
		FR.setH(false);
		FR.setZ(false);
		cpuReg.writeReg("A", result, false);
	}
	
	// Rotate register right through carry.
	public static void instruct_RR(String register) {
		int result = ((cpuReg.getReg(register) >> 1) & 0xFF);
		result = FR.isC() ? setBit(result, 7) : result;
		FR.setN(false);
		FR.setH(false);
		FR.setZ(result == 0);
		FR.setC(checkBitSet(cpuReg.getReg(register), 0));
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A right through carry.
	public static void instruct_RRA() {
		int result = ((cpuReg.getReg("A") >> 1) & 0xFF);
		result = FR.isC() ? setBit(result, 7) : result;
		FR.setN(false);
		FR.setH(false);
		FR.setZ(false);
		FR.setC(checkBitSet(cpuReg.getReg("A"), 0));
		cpuReg.writeReg("A", result, false);
	}
	
	// Rotate register right. Bit 0 from original value is moved to bit 7 and also stored as carry flag.
	public static void instruct_RRC(String register) {
		int result = ((cpuReg.getReg(register) >> 1) & 0xFF);
		result = checkBitSet(cpuReg.getReg(register), 0) ? setBit(result, 7) : result;
		FR.setC(checkBitSet(cpuReg.getReg(register), 0));
		FR.setN(false);
		FR.setH(false);
		FR.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A right. Bit 0 from original value is moved to bit 7 and also stored as carry flag.
	public static void instruct_RRCA() {
		int result = ((cpuReg.getReg("A") >> 1) & 0xFF);
		result = checkBitSet(cpuReg.getReg("A"), 0) ? setBit(result, 7) : result;
		FR.setC(checkBitSet(cpuReg.getReg("A"), 0));
		FR.setN(false);
		FR.setH(false);
		FR.setZ(false);
		cpuReg.writeReg("A", result, false);
	}
}