package us.kshadow.gbz80emu.processor;

/* Main logic for modified Z80 implementation from the original Gameboy.
 * @author Nicholas Bonet
 */

public class CPU {

	private final CPURegisters cpuRegisters;
	
	public CPU() {
		this.cpuRegisters = new CPURegisters();
	}
	
	// Base instruction implementations. Will be mapped to opcodes later on.
	
	public void instruct_OR(int arg) {
		int result = this.cpuRegisters.getA() | arg;
		this.cpuRegisters.getFR().setZ(result);
		this.cpuRegisters.getFR().setC(false);
		this.cpuRegisters.getFR().setN(false);
		this.cpuRegisters.getFR().setH(false);
		this.cpuRegisters.setA(result);
	}
	
	public void instruct_XOR(int arg) {
		int result = this.cpuRegisters.getA() ^ arg;
		this.cpuRegisters.getFR().setZ(result);
		this.cpuRegisters.getFR().setC(false);
		this.cpuRegisters.getFR().setN(false);
		this.cpuRegisters.getFR().setH(false);
		this.cpuRegisters.setA(result);
	}
	
	public void instruct_AND(int arg) {
		int result = this.cpuRegisters.getA() & arg;
		this.cpuRegisters.getFR().setZ(result);
		this.cpuRegisters.getFR().setC(false);
		this.cpuRegisters.getFR().setN(false);
		this.cpuRegisters.getFR().setH(true);
		this.cpuRegisters.setA(result);
	}
	
	public CPURegisters getCpuRegisters() {
		return cpuRegisters;
	}
}
