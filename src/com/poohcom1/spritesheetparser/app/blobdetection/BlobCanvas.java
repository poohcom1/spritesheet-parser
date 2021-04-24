package com.poohcom1.spritesheetparser.app.blobdetection;

import com.poohcom1.spritesheetparser.app.reusables.ImageCanvas;
import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlobCanvas extends ImageCanvas {
    // Tools
    public static String MERGE_TOOL = "merge";

    // Options
    private boolean _showBlobs = true;
    private boolean _showPoints = true;
    private boolean _showNumbers = true;
    private Color _blobColor = Color.RED;
    private Color _pointColor = new Color(0, 0, 255, 128);


    // Objects
    private BufferedImage image;
    private java.util.List<Blob> blobs;
    private java.util.List<Point> points;

    public BlobCanvas(BufferedImage image) {
        super(image.getWidth(), image.getHeight());

        maxMarqueeCount = -1;

        setImage(image);
        this.blobs = new ArrayList<>();
        this.points = new ArrayList<>();
    }

    @Override
    protected void endMarquee(List<Rect> marquees, Point pos) {
        if (marquees.size() > 0) {
            Rect marquee = getTrueMarqueesCoords().get(0);

            System.out.println(marquee + " Intersects: ");

            blobs.forEach(blob -> {
                if (marquee.contains(blob) || marquee.intersects(blob)) {
                    System.out.print(blobs.indexOf(blob) + ", ");
                }
            });

            System.out.println();
        }
        marquees.clear();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Stroke defaultStroke = ((Graphics2D) g).getStroke();

        int xOffset = getXOffset();
        int yOffset = getYOffset();

        g.drawImage(image, xOffset, yOffset, null);

        if (_showBlobs) {
            for (int i = 0; i < blobs.size(); i++) {
                Rect rect = blobs.get(i);
                g.setColor(_blobColor);
                ((Graphics2D)g).setStroke(new BasicStroke(
                        (float) (1.5f/xScale),                      // Width
                        BasicStroke.CAP_SQUARE,    // End cap
                        BasicStroke.JOIN_BEVEL,    // Join style
                        1.0f,                     // Miter limit
                        new float[] {(float) (2.0f), (float) (2.0f)},          // Dash pattern
                        0.1f));
                g.drawRect(rect.x + xOffset, rect.y + yOffset, rect.width+1, rect.height+1);

                if (_showNumbers) {

                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(i), rect.x + rect.width + xOffset, rect.y + rect.height + yOffset);
                }
            }
        }

        if (_showPoints) {
            for (Point point : points) {
                g.setColor(_pointColor);
                g.drawRect(point.x + xOffset, point.y + yOffset, 1, 1);
            }
        }

        drawMarquees(g);
    }

    public void setImage(BufferedImage image) {this.image = image; setSize(image.getWidth(), image.getHeight());}

    public void setBlobs(java.util.List<Blob> blobs) {
        this.blobs = blobs;
    }

    public void setPoints(List<Point> points) {
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
