package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ZoomablePanel extends JScrollPane {
    public float zoomAmount = 0.1f;
    public int panAmount = 5;

    private int previousX = -1;
    private int previousY = -1;

    private int mouseX = 0;
    private int mouseY = 0;

    public ZoomablePanel(ZoomableComponent children) {
        super(children);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setFocusable(true);
        setWheelScrollingEnabled(false);

        // Reset moving position
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                previousY = -1; previousX = -1;
            }
        });

        // THANK YOU MY BRO https://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position
        addMouseWheelListener(e -> {
            final float ZOOM_AMOUNT = 0.2f;

            float zoomFactor = ZOOM_AMOUNT;

            if (e.getWheelRotation() > 0 && children.xScale > 1.0) {
                children.zoomOut(ZOOM_AMOUNT);
                zoomFactor = -zoomFactor;
            } else if (e.getWheelRotation() < 0)
                children.zoomIn(ZOOM_AMOUNT);

            children.setPreferredSize(children.getScaledSize());
            children.setSize(children.getScaledSize());

            Point pos = this.getViewport().getViewPosition();

            int newX = (int)(e.getX()*(zoomFactor) + (1.0 + zoomFactor)*pos.x);
            int newY = (int)(e.getY()*(zoomFactor) + (1.0 + zoomFactor)*pos.y);
            this.getViewport().setViewPosition(new Point(newX, newY));
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX(); mouseY=e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if (previousX == -1) previousX = e.getXOnScreen();
                if (previousY == -1) previousY = e.getYOnScreen();

                int deltaX = e.getXOnScreen() - previousX;
                int deltaY = e.getYOnScreen() - previousY;

                previousX = e.getXOnScreen();
                previousY = e.getYOnScreen();

                moveViewport(-deltaX, -deltaY);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
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
        });
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
