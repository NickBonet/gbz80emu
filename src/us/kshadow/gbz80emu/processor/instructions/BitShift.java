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
	private static FlagRegister fr = cpuReg.getFR();
	
	private BitShift() { }
	
	// For swapping upper/lower halves in 8-bit registers.
	public static void instructSWAP(String register) {
		int result = ((cpuReg.getReg(register) & 0x0F) << 4 | (cpuReg.getReg(register) & 0xF0) >> 4);
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate left through carry flag
	public static void instructRL(String register) {
		int result = ((cpuReg.getReg(register) << 1) & 0xFF);
		result |= fr.isC() ? 1 : 0; // puts carry bit into bit 0 if set
		fr.setC(checkBitSet(cpuReg.getReg(register), 7));
		fr.setH(false);
		fr.setN(false);
		fr.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Similar to RL, except bit 0 is set to 0.
	public static void instructSLA(String register) {
		int result = ((cpuReg.getReg(register) << 1) & 0xFF);
		if (!checkBitSet(result, 0)) { setBit(result, 0); } // set bit 0 to 0 if it's not already.
		fr.setC(checkBitSet(cpuReg.getReg(register), 7));
		fr.setH(false);
		fr.setN(false);
		fr.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A register left through carry flag
	public static void instructRLA() {
		int result = ((cpuReg.getReg("A") << 1) & 0xFF);
		result |= fr.isC() ? 1 : 0;
		fr.setC(checkBitSet(cpuReg.getReg("A"), 7));
		fr.setH(false);
		fr.setN(false);
		fr.setZ(false);
		cpuReg.writeReg("A", result, false); 
	}
	
	// Rotate register left. Bit 7 from initial value goes into carry flag and bit 0.
	public static void instructRLC(String register) {
		int result = ((cpuReg.getReg(register) << 1) & 0xFF);
		result |= checkBitSet(cpuReg.getReg(register), 7) ? 1 : 0;
		fr.setC(checkBitSet(cpuReg.getReg(register), 7));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A left. Bit 7 from initial value goes into carry flag and bit 0.
	public static void instructRLCA() {
		int result = ((cpuReg.getReg("A") << 1) & 0xFF);
		result |= checkBitSet(cpuReg.getReg("A"), 7) ? 1 : 0;
		fr.setC(checkBitSet(cpuReg.getReg("A"), 7));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(false);
		cpuReg.writeReg("A", result, false);
	}
	
	// Rotate register right through carry.
	public static void instructRR(String register) {
		int result = ((cpuReg.getReg(register) >> 1) & 0xFF);
		result = fr.isC() ? setBit(result, 7) : result;
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		fr.setC(checkBitSet(cpuReg.getReg(register), 0));
		cpuReg.writeReg(register, result, false);
	}
	
	// Similar to RR, except bit 7 remains unmodified.
	public static void instructSRA(String register) {
		int result = ((cpuReg.getReg(register) >> 1) & 0xFF);
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		fr.setC(checkBitSet(cpuReg.getReg(register), 0));
		cpuReg.writeReg(register, result, false);
	}
	
	// Similar to SRA, except bit 7 is zeroed.
	public static void instructSRL(String register) {
		int result = ((cpuReg.getReg(register) >> 1) & 0xFF);
		if (!checkBitSet(result, 7)) { setBit(result, 7); } // set bit 7 to 0 if it's not already.
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		fr.setC(checkBitSet(cpuReg.getReg(register), 0));
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A right through carry.
	public static void instructRRA() {
		int result = ((cpuReg.getReg("A") >> 1) & 0xFF);
		result = fr.isC() ? setBit(result, 7) : result;
		fr.setN(false);
		fr.setH(false);
		fr.setZ(false);
		fr.setC(checkBitSet(cpuReg.getReg("A"), 0));
		cpuReg.writeReg("A", result, false);
	}
	
	// Rotate register right. Bit 0 from original value is moved to bit 7 and also stored as carry flag.
	public static void instructRRC(String register) {
		int result = ((cpuReg.getReg(register) >> 1) & 0xFF);
		result = checkBitSet(cpuReg.getReg(register), 0) ? setBit(result, 7) : result;
		fr.setC(checkBitSet(cpuReg.getReg(register), 0));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		cpuReg.writeReg(register, result, false);
	}
	
	// Rotate A right. Bit 0 from original value is moved to bit 7 and also stored as carry flag.
	public static void instructRRCA() {
		int result = ((cpuReg.getReg("A") >> 1) & 0xFF);
		result = checkBitSet(cpuReg.getReg("A"), 0) ? setBit(result, 7) : result;
		fr.setC(checkBitSet(cpuReg.getReg("A"), 0));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(false);
		cpuReg.writeReg("A", result, false);
	}
	
	// Tests specified bit in register, sets zero flag to 0 if bit tested is 0.
	public static void instructBIT(String register, int bitPos) {
		if (bitPos < 8) {
			if (!checkBitSet(cpuReg.getReg(register), bitPos)) { fr.setZ(true); }
			fr.setN(false);
			fr.setH(true);
		}
	}
	
	// Sets specified bit in byte to 1.
	public static void instructSET(String register, int bitPos) {
		if (bitPos < 8) {
			int result = cpuReg.getReg(register);
			if (!checkBitSet(result, bitPos)) { setBit(result, bitPos); }
			cpuReg.writeReg(register, result, false);
		}
	}
	
	// Sets specified bit in byte to 0.
	public static void instructRES(String register, int bitPos) {
		if (bitPos < 8) {
			int result = cpuReg.getReg(register);
			if (checkBitSet(result, bitPos)) { setBit(result, bitPos); }
			cpuReg.writeReg(register, result, false);
		}
	}
}
