package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.cv.Blob;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BlobCanvas extends JPanel {
    private final BufferedImage image;
    private Blob[] blobs;
    private Point[] points;

    private boolean show = true;

    public BlobCanvas(BufferedImage image) {
        this.image = image;
        this.blobs = new Blob[0];
        this.points = new Point[0];


        setSize(image.getWidth(), image.getHeight());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (int i = 0; i < blobs.length; i++) {
                    if (blobs[i].contains(e.getX(), e.getY()))
                        System.out.printf("#%d: %s\n", i, blobs[i].toString());
                }
            }
        });
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(),image.getHeight());
    }

    public void setBlobs(Blob[] blobs) {this.blobs = blobs;}

    public void setPoints(Point[] points) {this.points = points;}

    public void toggleBlobs() {show = !show; repaint();}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, null);

        if (show) {
            for (int i = 0; i < blobs.length; i++) {
                Rect rect = blobs[i];
                g.setColor(Color.red);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);

                g.setColor(Color.BLUE);
                g.drawString(String.valueOf(i), (int) (rect.x + rect.width*0.75), rect.y + rect.height);
            }

            for (Point point : points) {
                g.setColor(new Color(0, 0, 255, 22));
                g.drawRect(point.x, point.y, 1, 1);
            }
        }
    }


}
