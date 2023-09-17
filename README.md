# gbz80emu
A (potential) Game Boy emulator written in Java, for learning purposes.

## Progress
- [x] CPU register implementation
- [ ] MMU implementation (mostly complete, few address ranges dependent on later additions)
- [x] CPU instructions implemented.
- [x] Fetch-Decode-Execute cycle.
- [x] Blargg's CPU instruction tests passing
- [x] Blargg's CPU instruction timing passing
- [x] Able to execute bootROM fully.
- [x] PPU implementation.
  - [x] Able to draw tiles line by line to framebuffer.
  - [x] Very basic display output via background tiles only.
  - [ ] Drawing windows
  - [ ] Drawing sprites
  - [ ] Integrating registers properly