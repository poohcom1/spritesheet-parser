package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyCanvas extends JPanel {
    private final BufferedImage image;
    private Rect[] borders;
    private Point[] points;

    public MyCanvas(BufferedImage image) {
        this.image = image;
        this.borders = new Rect[0];
        this.points = new Point[0];

        setSize(image.getWidth(), image.getHeight());
    }
    public MyCanvas(BufferedImage image, Rect[] borders, Point[] points) {
        this.image = image;
        this.borders = borders;
        this.points = points;

        setSize(image.getWidth(), image.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(),image.getHeight());
    }

    public void setBorders(Rect[] borders) {this.borders = borders;}

    public void setPoints(Point[] points) {this.points = points;}

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
        for (Rect rect: borders) {
            g.setColor(Color.red);
            g.drawRect(rect.x1, rect.y1, rect.getWidth(), rect.getHeight());
        }

        for (Point point: points) {
            g.setColor(new Color(0, 0, 255, 82));
            g.drawRect(point.x, point.y, 1, 1);
        }
    }


}
