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
			cpu.getMMU().loadROM(testROM.getRomAsArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//cpu.getCpuReg().setInitValues();
		cpu.getCpuReg().printReg();
		
		// Beginning of the actual fetch/decode/execute cycle
		while(true) {
			cpu.fetchInstruction();
			cpu.getCpuReg().printReg();
		}
	}
}
