package com.poohcom1.spritesheetparser.app.blobdetection;

import com.poohcom1.spritesheetparser.app.reusables.ZoomableComponent;
import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.shapes2D.Point;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlobCanvas extends ZoomableComponent {
    // Options
    private boolean _showBlobs = true;
    private boolean _showPoints = true;
    private boolean _showNumbers = true;
    private Color _blobColor = Color.RED;
    private Color _pointColor = new Color(0, 0, 255, 128);

    public boolean doDrawMarquee = false;

    // Objects
    private BufferedImage image;
    private java.util.List<Blob> blobs;
    private java.util.List<com.poohcom1.spritesheetparser.util.shapes2D.Point> points;

    private List<Rect> marquees;
    private List<Point> pen;

    public BlobCanvas(BufferedImage image) {
        super(image.getWidth(), image.getHeight());

        setImage(image);
        this.blobs = new ArrayList<>();
        this.points = new ArrayList<>();
        marquees = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (doDrawMarquee) {
                    System.out.println(e.getButton());
                    startMarquee(e.getX(), e.getY());
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (doDrawMarquee) {
                    drawMarquee(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    public void startMarquee(int x, int y) {
        Rect newMarquee = new Rect(x, y, x, y);
        newMarquee.setAnchor();
        marquees.add(newMarquee);
    }

    public void drawMarquee(int x, int y) {
        if (marquees.isEmpty()) return;
        marquees.get(marquees.size()-1).resizeWithAnchor(x, y);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        int xOffset = (int) ((image.getWidth()* panelXScale - image.getWidth())/2) + panelOffset;
        int yOffset = (int) ((image.getHeight()* panelYScale - image.getHeight())/2) + panelOffset;

        Graphics2D g = zoomChildren(graphics);

        g.drawImage(image, xOffset, yOffset, null);

        if (_showBlobs) {
            for (int i = 0; i < blobs.size(); i++) {
                Rect rect = blobs.get(i);
                g.setColor(_blobColor);
                g.drawRect(rect.x + xOffset, rect.y + yOffset, rect.width, rect.height);

                if (_showNumbers) {
                    g.setColor(Color.BLUE);
                    g.drawString(String.valueOf(i), (int) (rect.x + rect.width * 0.75)  + xOffset, rect.y + rect.height + yOffset);
                }
            }
        }

        if (_showPoints) {
            for (Point point : points) {
                g.setColor(_pointColor);
                g.drawRect(point.x + xOffset, point.y + yOffset, 1, 1);
            }
        }

        g.setStroke(new BasicStroke(
                1.0f,                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] {3.0f,2.0f},          // Dash pattern
                0.0f));

        g.setColor(Color.BLACK);
        marquees.forEach(marquee -> g.drawRect(marquee.x, marquee.y, marquee.width, marquee.height));
    }

    public void setImage(BufferedImage image) {this.image = image; setSize(image.getWidth(), image.getHeight());}

    public void setBlobs(java.util.List<Blob> blobs) {
        this.blobs = blobs;
    }

    public void setPoints(List<com.poohcom1.spritesheetparser.util.shapes2D.Point> points) {
        this.points = points;
    }

    public void setShowBlobs(boolean showBlobs) {
        _showBlobs = showBlobs;
        repaint();
    }

    public void setShowNumbers(boolean showNumbers) {
        _showNumbers = showNumbers;
        repaint();
    }

    public void setShowPoints(boolean showPoints) {
        _showPoints = showPoints;
        repaint();
    }
}
