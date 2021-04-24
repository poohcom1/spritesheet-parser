package com.poohcom1.spritesheetparser.app.reusables;

import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageCanvas extends ZoomableComponent {
    // Tools
    public static final String MOVE_TOOL = "move";
    public static final String MARQUEE_TOOL = "marquee";
    public static final String PEN_TOOL = "pen";

    private Map<String, MouseAdapter> toolMap;

    // Objects
    protected int maxMarqueeCount;

    private List<Rect> marqueePoints;
    private List<Point> penPoints;

    private float _dashPhase;
    private float _dashInc = 0;

    ScheduledExecutorService animator;

    List<MouseAdapter> mouseCallbacks;
    MouseAdapter mousePressedToolCallback;

    public ImageCanvas(int width, int height) {
        super(width, height);

        maxMarqueeCount = 100;
        marqueePoints = new ArrayList<>();
        penPoints = new ArrayList<>();

        _dashPhase = 0.0f;
        animator = Executors.newScheduledThreadPool(1);

        animator.scheduleAtFixedRate(() -> {
            animatedDashPhase();
            repaint();
        }, 0, 16, TimeUnit.MILLISECONDS);

        toolMap = new HashMap<>();
        toolMap.put(MOVE_TOOL, moveToolCallback);
        toolMap.put(MARQUEE_TOOL, marqueeToolCallback);
    }

    public void addTool(String name, MouseAdapter callback) {
        toolMap.put(name, callback);
    }

    // Move tool
    public MouseAdapter moveToolCallback = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            parentPanel.setMouseMove(true);
        }
    };

    public MouseAdapter marqueeToolCallback = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            parentPanel.setMouseMove(false);

            if (SwingUtilities.isLeftMouseButton(e)/* && !parentPanel.panKeyPressed()*/) {
                startMarquee(marqueePoints, e.getPoint());
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragMarquee(marqueePoints, e.getPoint());
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            endMarquee(marqueePoints, e.getPoint());
        }
    };

    public void setTool(String tool) {
        removeMouseListener(mousePressedToolCallback);
        removeMouseMotionListener(mousePressedToolCallback);

        mousePressedToolCallback = toolMap.get(tool);

        addMouseListener(mousePressedToolCallback);
        addMouseMotionListener(mousePressedToolCallback);
    }

    private void animatedDashPhase() {
        _dashPhase += _dashInc;
        if (_dashPhase >= 4.0f) _dashPhase = 0f;
    }

    protected void startMarquee(List<Rect> marquees, Point point) {
        point = inverseTransformPoint(point);

        point = clampPoint(point);

        Rect newMarquee = new Rect(point, point);
        newMarquee.setAnchor();

        if (marquees.size() == maxMarqueeCount) {
            marquees.remove(0);
        }
        marquees.add(newMarquee);
    }

    protected void dragMarquee(List<Rect> marquees, Point pos) {
        if (marquees.isEmpty()) return;
        pos = inverseTransformPoint(pos);
        marquees.get(marquees.size() - 1).resizeWithAnchor(clampPoint(pos));
    }

    protected void endMarquee(List<Rect> marquees, Point pos) {
        if (maxMarqueeCount == -1) {
            marquees.clear();
        }
    }

    private Point clampPoint(Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
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


    protected void drawMarquees(Graphics g){
        marqueePoints.forEach(marquee -> {
            drawMarquee(g, marquee);
        });
    }

    protected void drawMarquee(Graphics g, Rect marquee) {
        double[] dashes = {4.0f, 1.0f};
        try {
            transform.inverseTransform(dashes, 0, dashes, 0, 1);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

        g.setColor(Color.BLACK);

        float[] floatDashes = {(float) dashes[0], (float) dashes[0]};
        _dashInc = (float) dashes[1];

        ((Graphics2D) g).setStroke(new BasicStroke(
                (float) (1.0f / xScale),                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_BEVEL,    // Join style
                1.0f,                     // Miter limit
                floatDashes,          // Dash pattern
                _dashPhase));

        g.drawRect(marquee.x, marquee.y, marquee.width, marquee.height);
    }

    protected Rect getTrueMarqueeCoords(Rect marquee) {
        return new Rect(marquee.x - getXOffset(),
                marquee.y - getYOffset(),
                marquee.x - getXOffset() + marquee.width,
                marquee.y - getYOffset() + marquee.height);
    }

    protected List<Rect> getTrueMarqueesCoords() {
        List<Rect> transformedMarquees = new ArrayList<>();

        for (Rect marquee : marqueePoints) {
            transformedMarquees.add(getTrueMarqueeCoords(marquee));
        }

        return transformedMarquees;
    }

    protected void drawGrid(Graphics g) {
        g.setColor(Color.gray);
        ((Graphics2D) g).setStroke(new BasicStroke(
                (float) (1.0f / xScale),                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_BEVEL));

        int cols = (int) (width / 4 );
        int rows = (int) (height / 4 );

        // draw the rows
        int rowHt = height / (rows);
        for (int i = 0; i < rows; i++)
            g.drawLine(getXOffset(), i * rowHt + getYOffset(), width + getXOffset()-1, i * rowHt + getYOffset());

        // draw the columns
        int rowWid = width / (cols);
        for (int i = 0; i < cols; i++)
            g.drawLine(i * rowWid + getXOffset(), getYOffset(), i * rowWid + getXOffset(), height + getYOffset()-1);
    }
}

