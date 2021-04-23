package com.poohcom1.spritesheetparser.app.imagetools;

import com.poohcom1.spritesheetparser.app.reusables.ImageCanvas;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImageToolsCanvas extends ImageCanvas {
    public final static String COLOR_PICKER_TOOL = "colorPicker";

    private final BufferedImage originalSpriteSheet;
    private BufferedImage spriteSheet;
    public Color backgroundColor = new Color(0, 0, 0, 0);
    public Color replacementColor = new Color(0,0,0,0);

    public ImageToolsCanvas(BufferedImage spriteSheet) {
        super(spriteSheet.getWidth(), spriteSheet.getHeight());
        maxMarqueeCount = 1;
        originalSpriteSheet = spriteSheet;
        this.spriteSheet = spriteSheet;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

            }
        });

        addTool(COLOR_PICKER_TOOL, colorPickerCallback);
        repaint();
    }

    protected MouseAdapter colorPickerCallback = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            Color color;

            try {
                Robot robot = new Robot();

                color = robot.getPixelColor(e.getXOnScreen(), e.getYOnScreen());
            } catch (AWTException awtException) {
                awtException.printStackTrace();
                color = new Color(0,0,0);
            }

            spriteSheet = ImageUtil.replaceColors(originalSpriteSheet, new int[] {color.getRGB()}, replacementColor.getRGB());
            backgroundColor = color;
        }
    };

    public Color getBackground() {
        return backgroundColor;
    }

    public BufferedImage crop() {
        BufferedImage crop;
        if (marquees.size() > 0) {
            Rect marquee = marquees.get(0);
            crop = spriteSheet.getSubimage(marquee.x - getXOffset(), marquee.y - getYOffset(), marquee.width, marquee.height);
        } else {
            crop = spriteSheet;
        }

        return crop;
    }

    public void replaceColors(int[] colorsToReplace, int color) {
        spriteSheet = ImageUtil.replaceColors(spriteSheet, colorsToReplace, color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawGrid(g);

        int xOffset = getXOffset();
        int yOffset = getYOffset();

        g.drawImage(spriteSheet, xOffset, yOffset, null);

        drawMarquees(g);
    }

}
