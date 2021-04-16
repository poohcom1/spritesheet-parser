package com.poohcom1.spritesheetparser.window.blobdetection;

import com.poohcom1.spritesheetparser.util.shapes2D.Rect;
import com.poohcom1.spritesheetparser.util.shapes2D.Point;
import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.window.ZoomableCanvas;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BlobCanvas extends ZoomableCanvas {
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
        return new Dimension((int) (image.getWidth() * xScale), (int) (image.getHeight() * yScale));
    }

    public void setBlobs(Blob[] blobs) {this.blobs = blobs;}

    public void setPoints(Point[] points) {this.points = points;}

    public void toggleBlobs() {show = !show; repaint();}

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = zoomedGraphic(graphics);

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
