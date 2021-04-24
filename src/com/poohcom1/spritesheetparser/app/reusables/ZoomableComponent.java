package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public abstract class ZoomableComponent extends JComponent {
    public final int width;
    public final int height;

    public double panelXScale = 1.0;
    public double panelYScale = 1.0;

    protected final int MARGINS_X = 50;
    protected final int MARGINS_Y = 50;

    // Zooming
    protected double xScale = 1.0;
    protected double yScale = 1.0;

    protected AffineTransform transform = new AffineTransform();

    protected ZoomablePanel parentPanel;

    public ZoomableComponent(int width, int height) {
        this.width = width;
        this.height = height;

        // Set children size based on margins
        panelXScale = (float) (width + MARGINS_X)/ width;
        panelYScale = (float) (height + MARGINS_Y)/height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    public void setParent(ZoomablePanel parentPanel) {this.parentPanel = parentPanel;}


    /**
     * Transform a position to the corresponding inverse affine transformed coordinate
     * @param pos A Point position
     * @return The transformed Point position
     */
    protected Point inverseTransformPoint(Point pos) {
        try {
            transform.inverseTransform(pos, pos);
            return pos;
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
            noninvertibleTransformException.printStackTrace();
        }
        return null;
    }

    protected Point transformPoint(Point pos) {
        transform.transform(pos, pos);
        return pos;
    }

    private int getScaledWidth() {
        return (int) (width*xScale*panelXScale);
    }

    private int getScaledHeight() {
        return (int) (height*yScale*panelYScale);
    }

    public double getXZoom() {
        return xScale;
    }

    public double getYZoom() {
        return yScale;
    }

    public void zoomIn(float zoomAmount) {
        xScale *= 1.0 + zoomAmount;
        yScale *= 1.0 + zoomAmount;
    }

    public void zoomOut(float zoomAmount) {
        xScale *= 1.0 - zoomAmount;
        yScale *= 1.0 - zoomAmount;
    }


    protected int getXOffset() {
        return (int) ((width *  panelXScale - width)/2);
    }

    protected int getYOffset() {
        return (int) ((height * panelYScale - height)/2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        transform = new AffineTransform();
        transform.scale(xScale, yScale);
        ((Graphics2D) g).transform(transform);
    }
}
