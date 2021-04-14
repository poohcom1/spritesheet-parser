package com.poohcom1.spritesheetparser.util.sprite;

import com.poohcom1.spritesheetparser.util.image.ImageUtil;

import java.awt.image.BufferedImage;

public class Sprite {
    int xOffset, yOffset;
    private final BufferedImage image;

    public Sprite(BufferedImage image) {
        this.image = image;
    }

    public Sprite(BufferedImage image, int xOffset, int yOffset) {
        this.image = image;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public BufferedImage getSprite() {
        return ImageUtil.alignImage(image, xOffset, yOffset);
    }

    public int getOriginalWidth() { return image.getWidth(); }

    public int getOriginalHeight() { return image.getHeight(); }

    public int getWidth() {
        return image.getWidth() - xOffset;
    }

    public int getHeight() {
        return image.getHeight() - yOffset;
    }
}
