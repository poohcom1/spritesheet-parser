package com.poohcom1.app.imagetools;

import com.poohcom1.app.reusables.ToolsCanvas;
import com.poohcom1.spritesheetparser.image.ImageUtil;
import com.poohcom1.spritesheetparser.shapes2D.Rect;

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

    public final static BufferedImage BLANK_CANVAS = new BufferedImage(175, 1, BufferedImage.TYPE_4BYTE_ABGR);

    private BufferedImage originalImage;
    private BufferedImage image;

    public Color SHADE_COLOR = new Color(101, 101, 101, 156);

    private final List<Color> backgroundColors;
    private final Color replacementColor = new Color(0,0,0,0);
    private int autoBackground;

    public ImageToolsCanvas(BufferedImage spriteSheet) {
        super(spriteSheet.getWidth(), spriteSheet.getHeight());

        backgroundColors = new ArrayList<>();
        autoBackground = ImageUtil.findBackgroundColor(spriteSheet)[0];

        maxMarqueeCount = 1;

        this.originalImage = spriteSheet;
        this.image = spriteSheet;

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
                image = (ImageUtil.replaceColorsBuffered(originalImage, colors, replacementColor.getRGB()));
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
            crop = image.getSubimage(marquee.x, marquee.y, marquee.width, marquee.height);
        } else {
            crop = image;
        }

        return crop;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawCanvasBackground(g);

        int xOffset = (int) (image.getWidth() *  panelXScale - image.getWidth())/2;
        int yOffset = (int) (image.getHeight() * panelYScale - image.getHeight())/2;

        g.drawImage(image, xOffset, yOffset, null);

        drawMarquees(g);
    }

    @Override
    protected void drawMarquee(Graphics g, Rect marquee) {

        Graphics2D g2 = ((Graphics2D) g);

        Rectangle shade = new Rectangle(getXOffset(), getYOffset(), image.getWidth(), image.getHeight());

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

    public void setImage(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();

        this.image = image;
        this.originalImage = image;

        initScale(width, height);

        autoBackground = ImageUtil.findBackgroundColor(image)[0];
        notifyUpdateListeners();
        repaint();
    }
}
