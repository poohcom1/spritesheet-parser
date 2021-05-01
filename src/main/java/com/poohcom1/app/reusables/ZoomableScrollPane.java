package com.poohcom1.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ZoomableScrollPane<C extends ZoomComponent> extends JScrollPane {
    public final int PAN_KEY = KeyEvent.VK_SPACE;
    public final int ZOOM_KEY = KeyEvent.VK_CONTROL;

    public final float ZOOM_AMOUNT = 0.1f;

    public int PAN_SPEED = 16;

    private int previousX = -1;
    private int previousY = -1;

    // Mouse events
    private boolean doMouseMove = true;
    private boolean doKeyMove = true;
    private boolean doMouseZoom = true;

    // Zoom
    private float minZoom = 1.0f;

    // Child component
    private final C child;

    @SuppressWarnings("unchecked")
    public ZoomableScrollPane(C zoomComponent) {
        super(zoomComponent);
         zoomComponent.setParent((ZoomableScrollPane<ZoomComponent>) this);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
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
            if (doMouseZoom) {
                zoomToPoint(e.getPoint(), e.getWheelRotation());
            }
            //centerZoom();
        });

        // Mouse listener for screen panning
        viewport.getView().addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (doMouseMove || SwingUtilities.isMiddleMouseButton(e)) mouseDragged_moveScreen(e);
            }
        });

        child = zoomComponent;
    }

    public void findMinZoom() {
        int childSize = child.width + child.marginX;
        int viewSize = getViewport().getViewSize().width;

        while (childSize > viewSize) {
            viewSize *= ZOOM_AMOUNT + 1;
            child.zoom(ZOOM_AMOUNT + 1);
        }

        while (childSize < viewSize) {
            viewSize *= 1-ZOOM_AMOUNT;
            child.zoom(1 - ZOOM_AMOUNT);
        }
    }

    public void centerViewToPoint() {
        Rectangle bounds = getViewport().getViewRect();
        //Then you need the size of the component, but once it's added to the scroll pane, you can get this from the view port...

        Dimension size = getViewport().getViewSize();
        //Now you need to calculate the centre position...

        int x = (size.width - bounds.width) / 2;
        int y = (size.height - bounds.height) / 2;

        if (child.height > child.width) {
            y = child.marginY/2;
        }

        //Then you need to simply adjust the view port position...
        getViewport().setViewPosition(new Point(x, y));
    }

    // THANK YOU MY BRO: users/1936928/absolom: https://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position
    private void zoomToPoint(Point zoomPoint, int zoomDirection) {
        float zoomFactor = 0;

        if (zoomDirection > 0) {
            JScrollBar vertical = getHorizontalScrollBar();
            if (vertical.getWidth() >= vertical.getMaximum() * 0.8) {
                return;
            }
            zoomFactor = 1 - ZOOM_AMOUNT;
            child.zoom(zoomFactor);
        } else if (zoomDirection < 0) {
            zoomFactor = 1 + ZOOM_AMOUNT;
            child.zoom(zoomFactor);
        }

        Point pos = getViewport().getViewPosition();

        int newX = (int) (zoomPoint.getX()*(zoomFactor - 1f) + (zoomFactor)*pos.x);
        int newY = (int) (zoomPoint.getY()*(zoomFactor - 1f) + (zoomFactor)*pos.y);

        getViewport().setViewPosition(new Point(newX, newY));
    }


    private void mouseDragged_moveScreen(MouseEvent e) {
        if (previousX == -1) previousX = e.getXOnScreen();
        if (previousY == -1) previousY = e.getYOnScreen();

        int deltaX = e.getXOnScreen() - previousX;
        int deltaY = e.getYOnScreen() - previousY;

        previousX = e.getXOnScreen();
        previousY = e.getYOnScreen();

        moveViewportWithinBounds(-deltaX, -deltaY);
    }

    private void moveViewportWithinBounds(int xMove, int yMove) {
        Point newPosition = viewport.getViewPosition();

        newPosition.x += xMove;
        newPosition.y += yMove;

        if (newPosition.x < 0) newPosition.x = 0;
        if (newPosition.y < 0) newPosition.y = 0;

        viewport.setViewPosition(newPosition);
    }


    public C getChild() {return child;}

    public void setMouseMove(boolean doMouseMove) {this.doMouseMove = doMouseMove;}
    public void setKeyMove(boolean doKeyMove) {this.doKeyMove = doKeyMove;}
    public void setMouseZoom(boolean doKeyMove) {this.doMouseZoom = doMouseMove;}
}
