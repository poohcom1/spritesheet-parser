# Spritesheet Parser

A simple tool to automatically animate sprites in a sprite sheet that isn't aligned with fixed rows and columns. Outputs an image sequence of the sprites that is ready to be animated.

## Download

Download the Windows executable or jar file from:
https://github.com/poohcom1/spritesheet-parser/releases/

## Features
- **Sprite detection**: Automatically detect sprites in a sprite sheet through blob detection
- **Auto ordering**: Orders sprite based on the positioning of the sprites relative to each other
- **Spritesheet editing**: Crop sprites from large sprite sheets before extracting, so no need for slow manual cropping

## Basic use
1. Crop out the sprite sequence you want to animated from the main sprite sheet with the crop tool (or used an already cropped sheet)
2. Use the "Re-detect" panel to let the program automatically figure out how to extract sprites. If sprites contains separated pixels, lower the "sprite count" so that the program detects larger blobs of sprites.
3. Align the sprites
4. Export to image sequence ready for use in your game or animation!
