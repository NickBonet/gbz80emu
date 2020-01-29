package us.kshadow.gbz80emu;
import us.kshadow.gbz80emu.processor.CPU;

public class Main {

	public static void main(String[] args) {
		CPU cpu = new CPU();
		
		// I just use this as a testing area until I make actual unit tests.
		
		//cpu.getCpuRegisters().setB(0xFF);
		//cpu.getCpuRegisters().setC(0xD0);
		cpu.getCpuRegisters().setAF(0xFFFD);
		System.out.println("Value of AF:");
		System.out.println(cpu.getCpuRegisters().getAF());
		
		System.out.println("Value of F:");
		System.out.println(cpu.getCpuRegisters().getF());
		
		//System.out.println(cpu.getCpuRegisters().getB());
		//System.out.println(cpu.getCpuRegisters().getC());
	}
	
}
