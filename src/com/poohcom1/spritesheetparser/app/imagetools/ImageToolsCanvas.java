package com.poohcom1.spritesheetparser.app.imagetools;

import com.poohcom1.spritesheetparser.app.App;
import com.poohcom1.spritesheetparser.app.reusables.ToolsCanvas;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageToolsCanvas extends ToolsCanvas {
    public final static String CROP_TOOL = "Crop";
    public final static String COLOR_PICKER_TOOL = "Set background color";

    public final static BufferedImage BLANK_CANVAS = new BufferedImage(175, 50, BufferedImage.TYPE_4BYTE_ABGR);

    private final BufferedImage originalSpriteSheet;
    private BufferedImage spriteSheet;

    public Color SHADE_COLOR = new Color(101, 101, 101, 156);

    private final List<Color> backgroundColors;
    private final Color replacementColor = new Color(0,0,0,0);
    private final int autoBackground;

    public ImageToolsCanvas() {
        super(175, 50);

        backgroundColors = new ArrayList<>();
        autoBackground = 0;

        maxMarqueeCount = 1;

        originalSpriteSheet = null;
        spriteSheet = null;


        addTool(MOVE_TOOL, moveToolCallback);
        addTool(CROP_TOOL, new MarqueeAdapter() {});
        addTool(COLOR_PICKER_TOOL, colorPickerCallback);
    }

    public ImageToolsCanvas(BufferedImage spriteSheet) {
        super(spriteSheet.getWidth(), spriteSheet.getHeight());

        backgroundColors = new ArrayList<>();
        autoBackground = ImageUtil.findBackgroundColor(spriteSheet)[0];

        maxMarqueeCount = 1;

        this.originalSpriteSheet = spriteSheet;
        this.spriteSheet = spriteSheet;

        addTool(MOVE_TOOL, moveToolCallback);
        addTool(CROP_TOOL, new MarqueeAdapter() {});
        addTool(COLOR_PICKER_TOOL, colorPickerCallback);
        repaint();
    }

    protected MouseAdapter colorPickerCallback = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            parentPanel.setMouseMove(false);

            System.out.println("Color picked");

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
            } else if (SwingUtilities.isRightMouseButton(e)) {
                backgroundColors.clear();
            }
            notifyUpdateListeners();
        }
    };


    public BufferedImage crop() {
        BufferedImage crop;
        if (getTrueMarqueesCoords().size() > 0) {
            Rect marquee = getTrueMarqueesCoords().get(0);
            crop = spriteSheet.getSubimage(marquee.x, marquee.y, marquee.width, marquee.height);
        } else {
            crop = spriteSheet;
        }

        return crop;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawClear(g);

        int xOffset = getXOffset();
        int yOffset = getYOffset();

        g.drawImage(spriteSheet, xOffset, yOffset, null);

        drawMarquees(g);
    }

    @Override
    protected void drawMarquee(Graphics g, Rect marquee) {

        Graphics2D g2 = ((Graphics2D) g);

        Rectangle shade = new Rectangle(getXOffset(), getYOffset(), spriteSheet.getWidth(), spriteSheet.getHeight());

        Area area = new Area(shade);

        area.subtract(new Area(marquee));

        g2.setColor(SHADE_COLOR);
        g2.fill(area);

    }

    public int[] getBackgroundColors() {
        int[] backgroundColorArray = new int[backgroundColors.size()];
        for (int i = 0; i < backgroundColorArray.length; i++) {
            backgroundColorArray[i] = backgroundColors.get(i).getRGB();
        }

        if (backgroundColorArray.length == 0) {
            return new int[] {autoBackground};
        }

        return backgroundColorArray;
    }
}
