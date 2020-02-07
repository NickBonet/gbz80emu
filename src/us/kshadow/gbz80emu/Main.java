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
		cpu.getCpuReg().printReg();	
		*/
		
		// instruct_RLA test based on manual
		cpu.getCpuReg().getFR().setC(true);
		cpu.getCpuReg().writeReg("A", 0x95, false);
		BitShift.instruct_RLA();
		System.out.println("instruct_RLA");
		cpu.getCpuReg().printReg();	
		
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		cpu.getCpuReg().writeReg("B", 0x85, false);
		BitShift.instruct_RLC("B");
		System.out.println("\ninstruct_RLC");
		cpu.getCpuReg().printReg();	
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		cpu.getCpuReg().writeReg("H", 0x00, false);
		BitShift.instruct_RLC("H");
		cpu.getCpuReg().printReg();	
		
		cpu.getCpuReg().clearRegs();
		System.out.println("\ninstruct_RR");
		cpu.getCpuReg().writeReg("A", 0x1, false);
		cpu.getCpuReg().writeReg("H", 0x8A, false);
		BitShift.instruct_RR("A");
		cpu.getCpuReg().printReg();	
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		BitShift.instruct_RR("H");
		cpu.getCpuReg().printReg();	
		
		cpu.getCpuReg().clearRegs();
		System.out.println("\ninstruct_RRA");
		cpu.getCpuReg().writeReg("A", 0x81, false);
		BitShift.instruct_RRA();
		cpu.getCpuReg().printReg();	
		
		cpu.getCpuReg().clearRegs();
		System.out.println("\ninstruct_RRC");
		cpu.getCpuReg().writeReg("A", 0x1, false);
		cpu.getCpuReg().writeReg("H", 0x0, false);
		BitShift.instruct_RRC("A");
		cpu.getCpuReg().printReg();	
		cpu.getCpuReg().getFR().flagsFromByte(0x00);
		BitShift.instruct_RRC("H");
		cpu.getCpuReg().printReg();	
	}
}
