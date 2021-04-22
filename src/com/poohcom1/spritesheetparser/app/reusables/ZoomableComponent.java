package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

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

    public void setParent(ZoomablePanel parentPanel) {this.parentPanel = parentPanel;}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    Dimension getScaledSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
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

    @Override
    protected void paintComponent(Graphics g) {
        transform = new AffineTransform();
        transform.scale(xScale, yScale);
        ((Graphics2D) g).transform(transform);
    }
}
