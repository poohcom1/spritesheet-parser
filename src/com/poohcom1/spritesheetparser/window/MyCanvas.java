package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyCanvas extends JPanel {
    private final BufferedImage image;
    private Rect[] borders;

    public MyCanvas(BufferedImage image, Rect[] borders) {
        this.image = image;
        this.borders = borders;

        setSize(image.getWidth(), image.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(),image.getHeight());
    }

    public void setBorders(Rect[] borders) {this.borders = borders;}

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
        for (Rect rect: borders) {
            g.setColor(Color.red);
            g.drawRect(rect.x1, rect.y1, rect.getWidth(), rect.getHeight());
        }
    }


}
