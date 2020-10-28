package us.kshadow.gbz80emu.processor.instructions;

import us.kshadow.gbz80emu.processor.CPURegisters;
import us.kshadow.gbz80emu.processor.FlagRegister;
import static us.kshadow.gbz80emu.util.BitUtil.checkBitSet;
import static us.kshadow.gbz80emu.util.BitUtil.setBit;

import us.kshadow.gbz80emu.memory.MMU;

/**
 * Instructions relating to bit shifts/rotations. (Mostly used in CB prefix instructions.)
 * @author Nicholas Bonet
 */

public class BitShift {

	private static final CPURegisters reg = CPURegisters.getInstance();
	private static final FlagRegister fr = reg.getFR();
	private static final MMU mmu = MMU.getInstance();
	
	private BitShift() { }
	
	/**
	 * SWAP - For swapping upper/lower halves in 8-bit registers.
	 * @param register - register/pointer for SWAP operation.
	 */
	public static void instructSWAP(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal & 0x0F) << 4 | (regVal & 0xF0) >> 4);
		fr.setZ(result == 0);
		fr.setC(false);
		fr.setN(false);
		fr.setH(false);
		reg.write(register, result);
	}
	
	/**
	 * RLCA - Rotate A left. Bit 7 from initial value goes into carry flag and bit 0.
	 */
	public static void instructRLCA() {
		int result = ((reg.read("A") << 1) & 0xFF);
		result |= checkBitSet(reg.read("A"), 7) ? 1 : 0;
		fr.setC(checkBitSet(reg.read("A"), 7));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(false);
		reg.write("A", result);
	}
	
	/**
	 * RLA - Rotate A register left through carry flag.
	 */
	public static void instructRLA() {
		int result = ((reg.read("A") << 1) & 0xFF);
		result |= fr.isC() ? 1 : 0;
		fr.setC(checkBitSet(reg.read("A"), 7));
		fr.setH(false);
		fr.setN(false);
		fr.setZ(false);
		reg.write("A", result); 
	}
	
	/**
	 * RRCA - Rotate A right. Bit 0 from original value is moved to bit 7 and also stored as carry flag.
	 */
	public static void instructRRCA() {
		int result = ((reg.read("A") >> 1) & 0xFF);
		result = checkBitSet(reg.read("A"), 0) ? setBit(result, 7) : result;
		fr.setC(checkBitSet(reg.read("A"), 0));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(false);
		reg.write("A", result);
	}
	
	/**
	 * RRA - Rotate A right through carry.
	 */
	public static void instructRRA() {
		int result = ((reg.read("A") >> 1) & 0xFF);
		result = fr.isC() ? setBit(result, 7) : result;
		fr.setN(false);
		fr.setH(false);
		fr.setZ(false);
		fr.setC(checkBitSet(reg.read("A"), 0));
		reg.write("A", result);
	}
	
	/**
	 * RLC - Rotate register left. Bit 7 from initial value goes into carry flag and bit 0.
	 * @param register - register/pointer for RLC operation.
	 */
	public static void instructRLC(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal << 1) & 0xFF);
		result |= checkBitSet(regVal, 7) ? 1 : 0;
		fr.setC(checkBitSet(regVal, 7));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		writeValue(register, result);
	}
	
	/**
	 * RL - Rotate left through carry flag.
	 * @param register - register/pointer for RL operation.
	 */
	public static void instructRL(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal << 1) & 0xFF);
		result |= fr.isC() ? 1 : 0; // puts carry bit into bit 0 if set
		fr.setC(checkBitSet(regVal, 7));
		fr.setH(false);
		fr.setN(false);
		fr.setZ(result == 0);
		writeValue(register, result);
	}
	
	/**
	 * RRC - Rotate register right. Bit 0 from original value is moved to bit 7 and also stored as carry flag.
	 * @param register - register/pointer for RRC operation.
	 */
	public static void instructRRC(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal >> 1) & 0xFF);
		result = checkBitSet(regVal, 0) ? setBit(result, 7) : result;
		fr.setC(checkBitSet(regVal, 0));
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		writeValue(register, result);
	}
	
	/**
	 * RR - Rotate register right through carry.
	 * @param register - register/pointer for RR operation.
	 */
	public static void instructRR(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal >> 1) & 0xFF);
		result = fr.isC() ? setBit(result, 7) : result;
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		fr.setC(checkBitSet(regVal, 0));
		writeValue(register, result);
	}
	
	/**
	 * SLA - Similar to RL, except bit 0 is set to 0.
	 * @param register - register/pointer for SLA operation.
	 */
	public static void instructSLA(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal << 1) & 0xFF);
		if (!checkBitSet(result, 0)) { setBit(result, 0); } // set bit 0 to 0 if it's not already.
		fr.setC(checkBitSet(regVal, 7));
		fr.setH(false);
		fr.setN(false);
		fr.setZ(result == 0);
		writeValue(register, result);
	}
	
	/**
	 * SRA - Similar to RR, except bit 7 remains unmodified.
	 * @param register - register/pointer for SRA operation.
	 */
	public static void instructSRA(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal >> 1) & 0xFF);
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		fr.setC(checkBitSet(regVal, 0));
		writeValue(register, result);
	}
	
	/**
	 * SRL - Similar to SRA, except bit 7 is zeroed.
	 * @param register - register/pointer for SRL operation.
	 */
	public static void instructSRL(String register) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		int result = ((regVal >> 1) & 0xFF);
		if (!checkBitSet(result, 7)) { setBit(result, 7); } // set bit 7 to 0 if it's not already.
		fr.setN(false);
		fr.setH(false);
		fr.setZ(result == 0);
		fr.setC(checkBitSet(regVal, 0));
		writeValue(register, result);
	}
	
	/**
	 * BIT - Tests specified bit in register, sets zero flag to 0 if bit tested is 0.
	 * @param register - register/pointer for BIT operation.
	 * @param bitPos - position of bit to test.
	 */
	public static void instructBIT(String register, int bitPos) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		if (bitPos < 8) {
			fr.setZ(!checkBitSet(regVal, bitPos));
			fr.setN(false);
			fr.setH(true);
		}
	}
	
	/**
	 * SET - Sets specified bit in byte to 1.
	 * @param register - register/pointer for SET operation.
	 * @param bitPos - position of bit to set.
	 */
	public static void instructSET(String register, int bitPos) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		if (bitPos < 8) {
			int result = regVal;
			if (!checkBitSet(result, bitPos)) { setBit(result, bitPos); }
			writeValue(register, result);
		}
	}
	
	/**
	 * RES - Sets specified bit in byte to 0.
	 * @param register - register/pointer for RES operation.
	 * @param bitPos - position of bit to set.
	 */
	public static void instructRES(String register, int bitPos) {
		int regVal = register.equals("HL") ? mmu.readByte(reg.read("HL")) : reg.read(register);
		if (bitPos < 8) {
			int result = regVal;
			if (checkBitSet(result, bitPos)) { setBit(result, bitPos); }
			writeValue(register, result);
		}
	}
	
	/**
	 * Writes changed value to its proper register or address in memory.
	 * @param register - register/pointer to write to.
	 * @param result - Value to write to register/pointer.
	 */
	private static void writeValue(String register, int result) {
		if(register.equals("HL")) {
			mmu.writeByte(reg.read("HL"), result);
		} else {
			reg.write(register, result);
		}
	}
}
