# gbz80emu
A (potential) Gameboy emulator written in Java, for learning purposes.

## Progress
- [x] CPU Register implementation
- [x] MMU implementation (mostly complete, few address ranges dependent on later additions)
- [x] Most CPU instructions implemented. Need to implement the following still:
  - [ ] EI
  - [ ] DI
  - [ ] HALT
  - [ ] STOP
  - [ ] RETI
  - [ ] DAA
- [ ] Fetch-Decode-Execute Cycle (in progress now)
- [ ] Able to execute bootROM