package com.poohcom1.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

public class ZoomComponent extends JComponent {
    public int width;
    public int height;

    public double panelXScale;
    public double panelYScale;

    protected final int MARGINS_X = 50;
    protected final int MARGINS_Y = 50;

    // Zooming
    protected double xScale = 1.0;
    protected double yScale = 1.0;

    protected AffineTransform transform = new AffineTransform();

    protected ZoomableScrollPane<ZoomComponent> parentPanel;

    // Listeners
    private final List<UpdateListener> updateListeners;

    public ZoomComponent(int width, int height) {
        this.width = width;
        this.height = height;

        updateListeners = new ArrayList<>();

        initScale(width, height);
    }

    protected void initScale(int width, int height) {
        // Set size based on margins
        panelXScale = (float) (width + MARGINS_X)/ width;
        panelYScale = (float) (height + MARGINS_Y)/height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getScaledWidth(), getScaledHeight());
    }

    public void setParent(ZoomableScrollPane<ZoomComponent> parentPanel) {this.parentPanel = parentPanel;}

    protected Point getCanvasPosition(Point pos) {
        return inverseTransformPoint(pos);
    }

    protected Point getPositionOnImage(Point pos) {
        Point transformedPoint = inverseTransformPoint(pos);
        transformedPoint.x -= getXOffset();
        transformedPoint.y -= getYOffset();
        return transformedPoint;
    }

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
        super.paintComponent(g);
        transform = new AffineTransform();
        transform.scale(xScale, yScale);
        ((Graphics2D) g).transform(transform);
    }

    protected void notifyUpdateListeners() {
        updateListeners.forEach(UpdateListener::onUpdate);
    }

    public void addUpdateListener(UpdateListener updateListener) {
        updateListeners.add(updateListener);
    }

    public void removeUpdateListener(UpdateListener updateListener) {
        updateListeners.remove(updateListener);
    }

    public interface UpdateListener {
        void onUpdate();
    }
}
