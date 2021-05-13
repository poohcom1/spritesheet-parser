# Spritesheet Parser

A simple tool to automatically animate sprites in sprite sheets that aren't aligned with fixed rows or columns. Outputs an image sequence of the sprites that is ready to be animated.

## Web App
https://spritesheet-parser.herokuapp.com/


## Download

Download the Windows executable or jar file from:
https://github.com/poohcom1/spritesheet-parser/releases/

## Features
- **Sprite detection**: Automatically detect sprites in a sprite sheet through blob detection
- **Auto ordering**: Orders sprite based on the positioning of the sprites relative to each other
- **Spritesheet editing**: Crop sprites from large sprite sheets before extracting, so no need for slow manual cropping

## Basic use
1. Load a sprite sheet and crop out the sprite sequence you want to animated from the main sprite sheet with the crop tool
2. Use the "Re-detect" panel to let the program automatically figure out how to extract sprites. If sprites contains separated pixels, lower the "sprite count" so that the program detects larger blobs of sprites.
3. Align the sprites
4. Export an image sequence that is ready for use in your game or animation!
