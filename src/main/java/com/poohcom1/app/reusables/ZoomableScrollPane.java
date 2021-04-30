package com.poohcom1.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ZoomableScrollPane<C extends ZoomComponent> extends JScrollPane {
    public final int PAN_KEY = KeyEvent.VK_SPACE;
    public final int ZOOM_KEY = KeyEvent.VK_CONTROL;

    public int PAN_SPEED = 16;

    private int previousX = -1;
    private int previousY = -1;

    // Mouse events
    private boolean doMouseMove = true;
    private boolean doKeyMove = true;
    private boolean doMouseZoom = true;

    // Child component
    private final C child;

    @SuppressWarnings("unchecked")
    public ZoomableScrollPane(C zoomComponent) {
        super(zoomComponent);
         zoomComponent.setParent((ZoomableScrollPane<ZoomComponent>) this);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setFocusable(true);

        setWheelScrollingEnabled(false);
        getVerticalScrollBar().setUnitIncrement(PAN_SPEED);

        // Reset moving position
        viewport.getView().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                previousY = -1; previousX = -1;
            }
        });

        // Mouse wheel listener for zooming
        addMouseWheelListener(e -> {
            if (doMouseZoom) mouseWheel_zoom(e);
        });

        // Mouse listener for screen panning
        viewport.getView().addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (doMouseMove || SwingUtilities.isMiddleMouseButton(e)) mouseDragged_moveScreen(e);
            }
        });

        child = zoomComponent;
    }


    public C getChild() {return child;}

    public void setMouseMove(boolean doMouseMove) {this.doMouseMove = doMouseMove;}
    public void setKeyMove(boolean doKeyMove) {this.doKeyMove = doKeyMove;}
    public void setMouseZoom(boolean doKeyMove) {this.doMouseZoom = doMouseMove;}

    public void centerZoom() {
        Rectangle bounds = getViewport().getViewRect();
        Dimension size = getViewport().getSize();

        int x = (size.width - bounds.width)/2;
        int y = (size.height - bounds.height)/2;
        getViewport().setViewPosition(new Point(x, y));
    }

    // THANK YOU MY BRO: users/1936928/absolom: https://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position
    private void mouseWheel_zoom(MouseWheelEvent e) {
        final float ZOOM_AMOUNT = 0.1f;

        float zoomFactor = 0;

        if (e.getWheelRotation() > 0) {
            if (child.getXZoom() <= 1.0) return;
            zoomFactor = 1 - ZOOM_AMOUNT;
            child.zoom(zoomFactor);
        } else if (e.getWheelRotation() < 0) {
            zoomFactor = 1 + ZOOM_AMOUNT;
            child.zoom(zoomFactor);
        }

        Point pos = getViewport().getViewPosition();

        int newX = (int) (e.getX()*(zoomFactor - 1f) + (zoomFactor)*pos.x);
        int newY = (int) (e.getY()*(zoomFactor - 1f) + (zoomFactor)*pos.y);

        getViewport().setViewPosition(new Point(newX, newY));
    }

    private void mouseDragged_moveScreen(MouseEvent e) {
        if (previousX == -1) previousX = e.getXOnScreen();
        if (previousY == -1) previousY = e.getYOnScreen();

        int deltaX = e.getXOnScreen() - previousX;
        int deltaY = e.getYOnScreen() - previousY;

        previousX = e.getXOnScreen();
        previousY = e.getYOnScreen();

        moveViewport(-deltaX, -deltaY);
    }

    private void moveViewport(int xMove, int yMove) {
        Point newPosition = viewport.getViewPosition();

        newPosition.x += xMove;
        newPosition.y += yMove;

        if (newPosition.x < 0) newPosition.x = 0;
        if (newPosition.y < 0) newPosition.y = 0;

        viewport.setViewPosition(newPosition);
    }

    private void setViewportPosition(int xPos, int yPos) {
        if (xPos < 0) xPos = 0;
        if (yPos < 0) yPos = 0;

        Point newPosition = new Point(xPos, yPos);

        viewport.setViewPosition(newPosition);
    }
}
