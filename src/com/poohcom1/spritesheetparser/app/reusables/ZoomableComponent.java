package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;

public abstract class ZoomableComponent extends JComponent {
    public final int width;
    public final int height;

    protected double panelScale = 10.0;
    public double panelXScale = 1.0;
    public double panelYScale = 1.0;
    protected int panelOffset = 0;

    protected double xScale = 1.0;
    protected double yScale = 1.0;

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
        return (int) (width*xScale* panelXScale + panelOffset *2);
    }

    private int getScaledHeight() {
        return (int) (height*yScale* panelYScale + panelOffset *2);
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
