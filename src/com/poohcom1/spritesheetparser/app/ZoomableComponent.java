package com.poohcom1.spritesheetparser.app;

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

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() > 0) {
                zoomOut(0.1f);
            } else if (e.getWheelRotation() < 0)
                zoomIn(0.1f);

            setPreferredSize(getScaledSize());
            setSize(getScaledSize());
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    private Dimension getScaledSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    private int getScaledWidth() {
        return (int) (width*xScale*PANEL_SCALE);
    }

    private int getScaledHeight() {
        return (int) (height*yScale*PANEL_SCALE);
    }

    public void zoomIn(float zoomAmount) {
        xScale += zoomAmount;
        yScale += zoomAmount;
    }

    public void zoomOut(float zoomAmount) {
        xScale -= zoomAmount;
        yScale -= zoomAmount;
    }

    protected Graphics2D zoomChildren(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        AffineTransform at = new AffineTransform();
        at.scale(xScale,yScale);
        g.transform(at);

        return g;
    }
}
