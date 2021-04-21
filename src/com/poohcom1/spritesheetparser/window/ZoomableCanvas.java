package com.poohcom1.spritesheetparser.window;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class ZoomableCanvas extends JPanel {
    protected double xScale = 1.0;
    protected double yScale = 1.0;

    public void zoomX (float zoom) {
        xScale = zoom;
    }

    public void zoomY (float zoom) {
        yScale = zoom;
    }

    public void zoomIn(float zoomAmount) {
        xScale += zoomAmount;
        yScale += zoomAmount;
    }

    public void zoomOut(float zoomAmount) {
        xScale -= zoomAmount;
        yScale -= zoomAmount;
    }


    protected Graphics2D zoomedGraphic(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        AffineTransform at = new AffineTransform();
        at.scale(xScale,yScale);
        g.transform(at);

        return g;
    }
}
