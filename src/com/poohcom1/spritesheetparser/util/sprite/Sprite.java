package com.poohcom1.spritesheetparser.util.sprite;

import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.image.BufferedImage;

public class Sprite extends Rect {
    private final BufferedImage image;

    public static final int LEFT_ALIGN = 0;
    public static final int CENTER_ALIGN_X = 1;
    public static final int RIGHT_ALIGN = 2;
    public static final int TOP_ALIGN = 3;
    public static final int CENTER_ALIGN_Y = 4;
    public static final int BOTTOM_ALIGN = 5;

    int xOffset;
    int yOffset;

    public Sprite(BufferedImage image) {
        this.image = image;

        xOffset = 0;
        yOffset = 0;

        width = image.getWidth();
        height = image.getHeight();
    }

    public Sprite(BufferedImage image, int xOffset, int yOffset) {
        this.image = image;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        width = image.getWidth();
        height = image.getHeight();
    }

    public Sprite(BufferedImage image, int xOffset, int yOffset, int width, int height) {
        this.image = image;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        this.width = width;
        this.height = height;
    }

    public void align(int alignment) {
        switch (alignment) {
            case LEFT_ALIGN -> leftAlign();
            case CENTER_ALIGN_X -> centerAlignX();
            case RIGHT_ALIGN -> rightAlign();
            case TOP_ALIGN -> topAlign();
            case CENTER_ALIGN_Y -> centerAlignY();
            case BOTTOM_ALIGN -> bottomAlign();
        }
    }


    public void centerAlignX() {
        xOffset = (width - image.getWidth())/2;
    }

    public void centerAlignY() {
        yOffset = (height - image.getHeight())/2;
    }

    public void leftAlign() {
        xOffset = 0;
    }

    public void rightAlign() {
        xOffset = width - image.getWidth();
    }

    public void topAlign() {
        yOffset = 0;
    }

    public void bottomAlign() {
        yOffset = height - image.getHeight();
    }

    public BufferedImage getSprite() {
        return ImageUtil.alignImage(image, xOffset, yOffset);
    }

    public int getOriginalWidth() { return image.getWidth(); }

    public int getOriginalHeight() { return image.getHeight(); }
}
