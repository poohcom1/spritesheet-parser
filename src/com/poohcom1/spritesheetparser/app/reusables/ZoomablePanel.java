package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ZoomablePanel extends JScrollPane {
    public final int PAN_KEY = KeyEvent.VK_CONTROL;

    public int panAmount = 50;

    private int previousX = -1;
    private int previousY = -1;

    public boolean panKeyPressed = false;

    private final int MARGINS_X = 300;
    private final int MARGINS_Y = 300;

    public boolean doMouseMove = true;
    public boolean doKeyMove = true;
    public boolean doMouseZoom = true;


    public ZoomablePanel(ZoomableComponent zoomComponent) {
        super(zoomComponent);
        zoomComponent.setParent(this);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setFocusable(true);
        setWheelScrollingEnabled(false);

        // Reset moving position
        viewport.getView().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                previousY = -1; previousX = -1;
            }
        });

        zoomComponent.panelXScale = (float) (zoomComponent.width + MARGINS_X)/ zoomComponent.width;
        zoomComponent.panelYScale = (float) (zoomComponent.height + MARGINS_Y)/zoomComponent.height;

        // THANK YOU MY BRO https://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position
        viewport.getView().addMouseWheelListener(e -> {
            if (doMouseZoom) mouseWheel_zoom(e, zoomComponent);
        });


        viewport.getView().addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }

            public void mouseDragged(MouseEvent e) {
                if (doMouseMove || panKeyPressed) mouseDragged_moveScreen(e);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == PAN_KEY) panKeyPressed = false;
            }

            public void keyPressed(KeyEvent e) {
                if (doKeyMove) keyPressed_moveScreen(e);

                panKeyPressed = panKeyPressed || e.getKeyCode() == PAN_KEY;
            }
        });
    }



    private void mouseWheel_zoom(MouseWheelEvent e, ZoomableComponent zoomComponent) {
        final float ZOOM_AMOUNT = 0.2f;

        float zoomFactor = ZOOM_AMOUNT;

        if (e.getWheelRotation() > 0) {
            zoomComponent.zoomOut(ZOOM_AMOUNT);
            zoomFactor = -zoomFactor;
        } else if (e.getWheelRotation() < 0)
            zoomComponent.zoomIn(ZOOM_AMOUNT);

        Point pos = this.getViewport().getViewPosition();

        int newX = (int)(e.getX()*(zoomFactor) + (1.0 + zoomFactor)*pos.x);
        int newY = (int)(e.getY()*(zoomFactor) + (1.0 + zoomFactor)*pos.y);
        this.getViewport().setViewPosition(new Point(newX, newY));
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

    private void keyPressed_moveScreen(KeyEvent e) {
        int moveX = 0;
        int moveY = 0;

        if (e.getKeyChar() == 'w') {
            moveY = -panAmount;
        }
        if (e.getKeyChar() == 's') {
            moveY = panAmount;
        }
        if (e.getKeyChar() == 'a') {
            moveX = -panAmount;
        }
        if (e.getKeyChar() == 'd') {
            moveX = panAmount;
        }

        moveViewport(moveX, moveY);
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
