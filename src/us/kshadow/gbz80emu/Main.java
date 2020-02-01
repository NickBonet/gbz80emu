package us.kshadow.gbz80emu;
import us.kshadow.gbz80emu.processor.CPU;
import us.kshadow.gbz80emu.processor.instructions.BitShift;
import us.kshadow.gbz80emu.util.BitUtil;

public class Main {

	public static void main(String[] args) {
		CPU cpu = new CPU();
		
		// I just use this as a testing area until I make actual unit tests.
		
		// instruct_RL test based on manual
		/*
		cpu.getCpuRegisters().setL(0x80);
		cpu.getCpuRegisters().setA(0x11);
		BitShift.instruct_RL("L");
		cpu.getCpuRegisters().getFR().setC(false);
		BitShift.instruct_RL("A");
		System.out.println("RL Test\n-------------");
		System.out.println("Value in L: " + cpu.getCpuRegisters().getL());
		System.out.println("Value in A: " + cpu.getCpuRegisters().getA());
		*/
		
		// instruct_RLA test based on manual
		cpu.getCpuReg().getFR().setC(true);
		cpu.getCpuReg().writeReg("A", 0x95, false);
		BitShift.instruct_RLA();
		System.out.println("Value in A: " + cpu.getCpuReg().getReg("A"));
		System.out.println(cpu.getCpuReg().getFR().isC());
	}
	
}
