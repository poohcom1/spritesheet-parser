package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BlobCanvas extends JPanel {
    private final BufferedImage image;
    private Rect[] borders;
    private Point[] points;

    private boolean show = true;

    public BlobCanvas(BufferedImage image) {
        this.image = image;
        this.borders = new Rect[0];
        this.points = new Point[0];


        setSize(image.getWidth(), image.getHeight());
    }

    public BlobCanvas(BufferedImage image, Rect[] borders, Point[] points) {
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

    public void toggleBlobs() {show = !show; repaint();}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, null);

        if (show) {
            for (Rect rect : borders) {
                g.setColor(Color.red);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
            }

            for (Point point : points) {
                g.setColor(new Color(0, 0, 255, 47));
                g.drawRect(point.x, point.y, 1, 1);
            }
        }
    }


}
