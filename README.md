# gbz80emu
A (potential) Gameboy emulator written in Java, for learning purposes.

## Progress
- [x] CPU register implementation
- [x] MMU implementation (mostly complete, few address ranges dependent on later additions)
- [x] Most CPU instructions implemented. Need to implement the following still:
  - [ ] EI
  - [ ] DI
  - [ ] HALT
  - [x] STOP
  - [ ] RETI
  - [ ] DAA
- [ ] Fetch-Decode-Execute Cycle (in progress now).
- [x] Able to execute bootROM fully.
- [x] PPU implementation.
  - [x] Able to draw tiles line by line to framebuffer.
  - [x] Very basic display output via background tiles only.
  - [ ] Drawing windows
  - [ ] Drawing sprites
  - [ ] Integrating registers properly