package com.poohcom1.spritesheetparser.app.reusables;

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
    protected int maxMarqueeCount;

    protected List<Rect> marquees;
    protected List<Point> penPoints;

    private float _dashPhase;

    ScheduledExecutorService animator;

    public ImageCanvas(int width, int height) {
        super(width, height);

        maxMarqueeCount = 100;
        marquees = new ArrayList<>();
        penPoints = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point mousePos = transformedMousePos(e);

                parentPanel.setMouseMove(false);

                switch (toolIndex) {
                    case MOVE -> parentPanel.setMouseMove(true);
                    case MARQUEE -> {
                        if (e.getButton() == MouseEvent.BUTTON1 && !parentPanel.panKeyPressed()) {
                            startMarquee(mousePos);
                            repaint();
                        }
                    }
                    case PEN -> {}
                }


            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point mousePos = transformedMousePos(e);

                switch (toolIndex) {
                    case MARQUEE -> {
                        if (parentPanel.m1Pressed() && !parentPanel.panKeyPressed()) {
                            dragMarquee(mousePos);
                            repaint();
                        }
                    }
                    case PEN -> {}
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

    public void startMarquee(Point point) {
        point = clampPoint(point);

        Rect newMarquee = new Rect(point, point);
        newMarquee.setAnchor();

        if (marquees.size() == maxMarqueeCount) {
            marquees.remove(0);
        }
        marquees.add(newMarquee);
    }

    public void dragMarquee(Point pos) {
        if (marquees.isEmpty()) return;
        marquees.get(marquees.size()-1).resizeWithAnchor(clampPoint(pos));
    }

    private Point clampPoint(Point point) {
        int x = (int) point.getX(); int y = (int) point.getY();
        if (x < getXOffset()) x = getXOffset();
        if (y < getYOffset()) y = getYOffset();
        if (x > getXOffset() + width) x = getXOffset() + width;
        if (y > getYOffset() + height) y = getYOffset() + height;
        return new Point(x, y);
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
