package com.poohcom1.spritesheetparser.app;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ZoomablePanel extends JScrollPane {
    public float zoomAmount = 0.1f;
    public int panAmount = 5;

    private int previousX = -1;
    private int previousY = -1;

    public ZoomablePanel(ZoomableComponent children) {
        super(children);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setFocusable(true);
        setWheelScrollingEnabled(false);

        children.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                previousY = -1; previousX = -1;
            }
        });

        children.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                int currentX = viewport.getViewPosition().x;
                int currentY = viewport.getViewPosition().y;

                if (previousX == -1) previousX = e.getXOnScreen();
                if (previousY == -1) previousY = e.getYOnScreen();

                int deltaX = e.getXOnScreen() - previousX;
                int deltaY = e.getYOnScreen() - previousY;

                previousX = e.getXOnScreen();
                previousY = e.getYOnScreen();

                Point newPos = new Point(currentX - deltaX, currentY - deltaY);
                if (newPos.x < 0) newPos.x = 0;
                if (newPos.y < 0) newPos.y = 0;

                viewport.setViewPosition(newPos);
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

                Point newPoint = viewport.getViewPosition();
                newPoint.x += moveX;
                newPoint.y += moveY;

                viewport.setViewPosition(newPoint);
            }
        });
    }


}
