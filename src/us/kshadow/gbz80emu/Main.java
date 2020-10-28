package us.kshadow.gbz80emu;
import java.io.IOException;

import us.kshadow.gbz80emu.memory.ROMParser;
import us.kshadow.gbz80emu.processor.CPU;

public class Main {

	public static void main(String[] args) {
		// Initialize CPU, load a ROM into MMU to prepare for execution loop.
		
		CPU cpu = new CPU();
		ROMParser testROM = new ROMParser();
		
		try {
			testROM.loadROM("tetris.gb");		
			cpu.getMMU().loadROM(testROM.getROMAsArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//cpu.getRegisters().setInitValues();
		cpu.getRegisters().print();
		
		// Beginning of the actual fetch/decode/execute cycle
		while(true) {
			if(cpu.isRunning()) {
				cpu.fetchInstruction();
				cpu.getRegisters().print();
			}
		}
	}
}
