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

    // Zooming
    protected double xScale = 1.0;
    protected double yScale = 1.0;

    protected AffineTransform transform = new AffineTransform();

    protected ZoomablePanel parentPanel;

    public ZoomableComponent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    public void setParent(ZoomablePanel parentPanel) {this.parentPanel = parentPanel;}

    protected Point2D transformedMousePos(MouseEvent e) {
        Point2D mousePos = e.getPoint();
        try {
            transform.inverseTransform(mousePos, mousePos);
            return mousePos;
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
            noninvertibleTransformException.printStackTrace();
        }
        return null;
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
