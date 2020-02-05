package us.kshadow.gbz80emu;
import us.kshadow.gbz80emu.processor.CPU;
import us.kshadow.gbz80emu.processor.instructions.BitShift;

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
		System.out.println("instruct_RLA");
		System.out.println("Value in A: " + cpu.getCpuReg().getReg("A"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		
		System.out.println("-----------------------------------");
		
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		cpu.getCpuReg().writeReg("B", 0x85, false);
		BitShift.instruct_RLC("B");
		System.out.println("instruct_RLC");
		System.out.println("Value in B: " + cpu.getCpuReg().getReg("B"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		cpu.getCpuReg().writeReg("H", 0x00, false);
		BitShift.instruct_RLC("H");
		System.out.println("Value in H: " + cpu.getCpuReg().getReg("H"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		
		cpu.getCpuReg().clearRegs();
		System.out.println("\ninstruct_RR");
		cpu.getCpuReg().writeReg("A", 0x1, false);
		cpu.getCpuReg().writeReg("H", 0x8A, false);
		BitShift.instruct_RR("A");
		System.out.println("Value in A: " + cpu.getCpuReg().getReg("A"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		BitShift.instruct_RR("H");
		System.out.println("Value in H: " + cpu.getCpuReg().getReg("H"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		
		cpu.getCpuReg().clearRegs();
		System.out.println("\ninstruct_RRA");
		cpu.getCpuReg().writeReg("A", 0x81, false);
		BitShift.instruct_RRA();
		System.out.println("Value in A: " + cpu.getCpuReg().getReg("A"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		
		cpu.getCpuReg().clearRegs();
		System.out.println("\ninstruct_RRC");
		cpu.getCpuReg().writeReg("A", 0x1, false);
		cpu.getCpuReg().writeReg("H", 0x0, false);
		BitShift.instruct_RRC("A");
		System.out.println("Value in A: " + cpu.getCpuReg().getReg("A"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		BitShift.instruct_RRC("H");
		System.out.println("Value in H: " + cpu.getCpuReg().getReg("H"));
		System.out.println("FR: " + cpu.getCpuReg().getFR().flagsAsByte());
	}
}
