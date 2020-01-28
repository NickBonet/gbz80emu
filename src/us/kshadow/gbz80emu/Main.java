package us.kshadow.gbz80emu;
import us.kshadow.gbz80emu.processor.CPU;

public class Main {

	public static void main(String[] args) {
		CPU cpu = new CPU();
		
		cpu.getCpuRegisters().setA(0xFF);
		cpu.getCpuRegisters().setF(0x10);
		System.out.println("Value of A << 8:");
		System.out.println(cpu.getCpuRegisters().getAF());
		
		cpu.getCpuRegisters().setPc(0xFFFF);
		System.out.println("Value of PC:");
		System.out.println(cpu.getCpuRegisters().getPc());
	}
	
}
