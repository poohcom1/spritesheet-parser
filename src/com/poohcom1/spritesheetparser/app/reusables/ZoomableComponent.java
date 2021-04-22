package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class ZoomableComponent extends JComponent {
    private final int width;
    private final int height;

    protected final double PANEL_SCALE = 1.5;

    protected double xScale = 1.0;
    protected double yScale = 1.0;

    public ZoomableComponent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    Dimension getScaledSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    private int getScaledWidth() {
        return (int) (width*xScale*PANEL_SCALE);
    }

    private int getScaledHeight() {
        return (int) (height*yScale*PANEL_SCALE);
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

    protected Graphics2D zoomChildren(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        AffineTransform at = new AffineTransform();
        at.scale(xScale,yScale);
        g.transform(at);

        return g;
    }
}
