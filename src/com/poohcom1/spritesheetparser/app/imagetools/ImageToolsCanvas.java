package com.poohcom1.spritesheetparser.app.imagetools;

import com.poohcom1.spritesheetparser.app.reusables.ImageCanvas;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageToolsCanvas extends ImageCanvas {

    private final BufferedImage spriteSheet;

    public ImageToolsCanvas(BufferedImage spriteSheet) {
        super(spriteSheet.getWidth(), spriteSheet.getHeight());
        maxMarqueeCount = 1;
        this.spriteSheet = spriteSheet;
        repaint();
    }

    public BufferedImage crop() {
        Rect marquee = marquees.get(0);

        return spriteSheet.getSubimage(marquee.x - getXOffset(), marquee.y - getYOffset(), marquee.width, marquee.height);
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
