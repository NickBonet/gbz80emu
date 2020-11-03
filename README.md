# gbz80emu
A (potential) Gameboy emulator written in Java, for learning purposes.

## Progress
- [x] CPU register implementation
- [x] MMU implementation (mostly complete, few address ranges dependent on later additions)
- [x] Most CPU instructions implemented. Need to implement the following still:
  - [ ] HALT
  - [ ] STOP (do it properly)
- [ ] Fetch-Decode-Execute Cycle (in progress now).
- [ ] Blargg's CPU instruction tests passing:
    - [x] 01-special
    - [ ] 02-interrupts (need to implement timer for this one)
    - [x] 03-op sp, hl
    - [x] 04-op r, imm
    - [x] 05-op rp
    - [x] 06-ld r, r
    - [x] 07-jr,jp,call,ret,rst
    - [x] 08-misc instrs
    - [x] 09-op r,r
    - [x] 10-bit ops
    - [x] 11-op a, (hl)
- [x] Able to execute bootROM fully.
- [x] PPU implementation.
  - [x] Able to draw tiles line by line to framebuffer.
  - [x] Very basic display output via background tiles only.
  - [ ] Drawing windows
  - [ ] Drawing sprites
  - [ ] Integrating registers properly