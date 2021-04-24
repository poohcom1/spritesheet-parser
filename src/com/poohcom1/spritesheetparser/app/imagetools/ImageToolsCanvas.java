package com.poohcom1.spritesheetparser.app.imagetools;

import com.poohcom1.spritesheetparser.app.reusables.ImageCanvas;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageToolsCanvas extends ImageCanvas {
    public final static String COLOR_PICKER_TOOL = "colorPicker";

    private final BufferedImage originalSpriteSheet;
    private BufferedImage spriteSheet;
    public List<Color> backgroundColors;
    public Color replacementColor = new Color(0,0,0,0);

    public ImageToolsCanvas(BufferedImage spriteSheet) {
        super(spriteSheet.getWidth(), spriteSheet.getHeight());

        backgroundColors = new ArrayList<>();

        maxMarqueeCount = 1;

        originalSpriteSheet = spriteSheet;
        this.spriteSheet = spriteSheet;

        addTool(COLOR_PICKER_TOOL, colorPickerCallback);
        repaint();
    }

    protected MouseAdapter colorPickerCallback = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            parentPanel.setMouseMove(false);

            if (SwingUtilities.isLeftMouseButton(e)) {
                Color color;

                try {
                    Robot robot = new Robot();

                    color = robot.getPixelColor(e.getXOnScreen(), e.getYOnScreen());
                } catch (AWTException awtException) {
                    awtException.printStackTrace();
                    color = new Color(0, 0, 0);
                }

                if (backgroundColors.size() == 0) backgroundColors.add(color);
                else backgroundColors.set(0, color);

                int[] colors = new int[backgroundColors.size()];
                for (int i = 0; i < colors.length; i++) {
                    colors[i] = backgroundColors.get(i).getRGB();
                }
                spriteSheet = (ImageUtil.replaceColorsBuffered(originalSpriteSheet, colors, replacementColor.getRGB()));
                System.out.println("ImageToolsCanvas: Color replaced!");
            } else if (SwingUtilities.isRightMouseButton(e)) {
                backgroundColors.clear();
            }
        }
    };


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


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int xOffset = getXOffset();
        int yOffset = getYOffset();

        g.drawImage(spriteSheet, xOffset, yOffset, null);

        drawMarquees(g);
    }

    @Override
    protected void drawMarquees(Graphics g) {
        //Rect marquee = marquees.get(0);


    }
}
