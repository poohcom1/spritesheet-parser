package com.poohcom1.spritesheetparser.app.reusables;

import com.poohcom1.spritesheetparser.util.shapes2D.Point;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageCanvas extends ZoomableComponent {
    // Tools
    public enum Tool {
        MOVE, MARQUEE, PEN
    }

    public static final int MOVE_TOOL = 0;
    public static final int MARQUEE_TOOL = 1;
    public static final int PEN_TOOL = 2;

    private Tool toolIndex = Tool.MOVE;

    // Objects
    protected List<Rect> marquees;
    protected List<Point> penPoints;

    private float _dashPhase;

    ScheduledExecutorService animator;

    public ImageCanvas(int width, int height) {
        super(width, height);

        marquees = new ArrayList<>();
        penPoints = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point2D mousePos = transformedMousePos(e);

                parentPanel.setMouseMove(false);

                switch (toolIndex) {
                    case MOVE -> {
                        parentPanel.setMouseMove(true);
                    }
                    case MARQUEE -> {
                        if (e.getButton() == MouseEvent.BUTTON1 && !parentPanel.panKeyPressed()) {
                            startMarquee((int) (mousePos.getX()), (int) (mousePos.getY()));
                            repaint();
                        }
                    }
                    case PEN -> {}
                }


            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point2D mousePos = transformedMousePos(e);

                switch (toolIndex) {
                    case MARQUEE -> {
                        if (parentPanel.m1Pressed() && !parentPanel.panKeyPressed()) {
                            dragMarquee((int) (mousePos.getX()), (int) (mousePos.getY()));
                            repaint();
                        }
                    }
                }
            }
        });

        _dashPhase = 0.0f;
        animator = Executors.newScheduledThreadPool(1);

        animator.scheduleAtFixedRate(() -> {

            animatedDashPhase(0.1f);
            repaint();

        }, 0, (long) 16, TimeUnit.MILLISECONDS);
    }

    public void setTool(int toolIndex) {this.toolIndex = Tool.values()[toolIndex];}

    private void animatedDashPhase(float inc) {
        _dashPhase += inc;
        if (_dashPhase >= 4.0f) _dashPhase = 0f;
    }

    public void startMarquee(int x, int y) {
        if (x < getXOffset()) x = getXOffset();
        if (y < getYOffset()) y = getYOffset();
        if (x > getXOffset() + width) x = getXOffset() + width;
        if (y > getYOffset() + height) y = getYOffset() + height;

        Rect newMarquee = new Rect(x, y, x, y);
        newMarquee.setAnchor();
        marquees.add(newMarquee);
    }

    public void dragMarquee(int x, int y) {
        if (marquees.isEmpty()) return;
        if (x < getXOffset()) x = getXOffset();
        if (y < getYOffset()) y = getYOffset();
        if (x > getXOffset() + width) x = getXOffset() + width;
        if (y > getYOffset() + height) y = getYOffset() + height;
        marquees.get(marquees.size()-1).resizeWithAnchor(x, y);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    protected void drawMarquees(Graphics g) {
        ((Graphics2D)g).setStroke(new BasicStroke(
                (float) (1.0f/xScale),                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_BEVEL,    // Join style
                1.0f,                     // Miter limit
                new float[] {(float) (2.0f), (float) (2.0f)},          // Dash pattern
                _dashPhase));

        g.setColor(Color.BLACK);
        marquees.forEach(marquee -> g.drawRect(marquee.x, marquee.y, marquee.width, marquee.height));
    }
}
