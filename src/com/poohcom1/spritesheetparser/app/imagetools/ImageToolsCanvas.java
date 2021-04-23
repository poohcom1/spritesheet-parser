package com.poohcom1.spritesheetparser.app.imagetools;

import com.poohcom1.spritesheetparser.app.reusables.ImageCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageToolsCanvas extends ImageCanvas {

    private final BufferedImage spriteSheet;

    public ImageToolsCanvas(BufferedImage spriteSheet) {
        super(spriteSheet.getWidth(), spriteSheet.getHeight());
        this.spriteSheet = spriteSheet;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int xOffset = getXOffset();
        int yOffset = getYOffset();

        g.drawImage(spriteSheet, xOffset, yOffset, null);

        drawMarquees(g);
    }
}
