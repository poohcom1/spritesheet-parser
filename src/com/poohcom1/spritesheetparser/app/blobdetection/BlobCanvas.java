package com.poohcom1.spritesheetparser.app.blobdetection;

import com.poohcom1.spritesheetparser.app.ZoomableComponent;
import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.shapes2D.Point;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlobCanvas extends ZoomableComponent {
    // Options
    private boolean _showBlobs = true;
    private boolean _showPoints = true;
    private Color _blobColor = Color.RED;
    private Color _pointColor = new Color(0, 0, 255, 128);

    // Objects
    private BufferedImage image;
    private java.util.List<Blob> blobs;
    private java.util.List<com.poohcom1.spritesheetparser.util.shapes2D.Point> points;

    public BlobCanvas(BufferedImage image) {
        super(image.getWidth(), image.getHeight());

        setImage(image);
        this.blobs = new ArrayList<>();
        this.points = new ArrayList<>();
    }

    public void setImage(BufferedImage image) {this.image = image; setSize(image.getWidth(), image.getHeight());}

    public void setBlobs(java.util.List<Blob> blobs) {
        this.blobs = blobs;
    }

    public void setPoints(List<com.poohcom1.spritesheetparser.util.shapes2D.Point> points) {
        this.points = points;
    }

    public void toggleBlobs() {
        _showBlobs = !_showBlobs;
        repaint();
    }

    public void togglePoints() {
        _showPoints = !_showPoints;
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        int xOffset = (int) ((image.getWidth()*PANEL_SCALE - image.getWidth())/2);
        int yOffset = (int) ((image.getHeight()*PANEL_SCALE - image.getHeight())/2);

        Graphics2D g = zoomChildren(graphics);

        g.drawImage(image, xOffset, yOffset, null);

        if (_showBlobs) {
            for (int i = 0; i < blobs.size(); i++) {
                Rect rect = blobs.get(i);
                g.setColor(_blobColor);
                g.drawRect(rect.x + xOffset, rect.y + yOffset, rect.width, rect.height);

                g.setColor(Color.BLUE);
                g.drawString(String.valueOf(i), (int) (rect.x + rect.width * 0.75)  + xOffset, rect.y + rect.height + yOffset);
            }
        }

        if (_showPoints) {
            for (Point point : points) {
                g.setColor(_pointColor);
                g.drawRect(point.x + xOffset, point.y + yOffset, 1, 1);
            }
        }
    }
}
