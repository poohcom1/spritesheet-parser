package com.poohcom1.spritesheetparser.app.reusables;

import com.poohcom1.spritesheetparser.shapes2D.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ToolsCanvas extends ZoomComponent {
    // Tools
    public static final String NO_TOOL = "None";
    public static final String MOVE_TOOL = "Move";
    public static final String PEN_TOOL = "Pen";

    private final Map<String, MouseAdapter> toolMap;


    protected int maxMarqueeCount;

    private final List<Rect> marqueePoints;
    private final List<Point> penPoints;

    private float _dashPhase;
    private float _dashInc = 0;

    ScheduledExecutorService animator;
    MouseAdapter mouseToolCallback;

    private Color canvasColor = Color.white;

    // Scale
    protected Dimension screenSize;

    List<ToolChangeListener> toolChangeListeners;


    public ToolsCanvas(int width, int height) {
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

        toolChangeListeners = new ArrayList<>();
        toolMap = new LinkedHashMap<>();

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }

    public void addTool(String name, MouseAdapter callback) {
        toolMap.put(name, callback);
    }

    // DEFAULT TOOLS ============================================================

    // Move tool
    public MouseAdapter moveToolCallback = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            parentPanel.setMouseMove(true);
        }
    };

    // Marquee tool
    protected class MarqueeAdapter extends MouseAdapter {
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

        protected void startMarquee(List<Rect> marquees, Point point) {
            point = getCanvasPosition(point);

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
            pos = getCanvasPosition(pos);
            marquees.get(marquees.size() - 1).resizeWithAnchor(clampPoint(pos));
        }

        protected void endMarquee(List<Rect> marquees, Point pos) {
            if (maxMarqueeCount == -1) {
                marquees.clear();
            }
        }
    }

    // TOOLS CONTROLS ======================================================================

    public Set<String> getToolConstants() {
        return toolMap.keySet();
    }

    public void setTool(String tool) {
        removeMouseListener(mouseToolCallback);
        removeMouseMotionListener(mouseToolCallback);

        mouseToolCallback = toolMap.get(tool);
        System.out.println("Switched to " + tool + ".");
        setCursor(setToolCursor(tool));

        toolChangeListeners.forEach(t -> t.onToolChange(tool));

        addMouseListener(mouseToolCallback);
        addMouseMotionListener(mouseToolCallback);
    }

    protected Cursor setToolCursor(String tool) {
        Cursor cursor;

        switch (tool) {
            case MOVE_TOOL:
                cursor = new Cursor(Cursor.MOVE_CURSOR);
                break;
            case NO_TOOL:
            default:
                cursor = new Cursor(Cursor.DEFAULT_CURSOR);
        }

        return cursor;
    }

    // Tool Change
    public void addToolChangeListener(ToolChangeListener t) {
        toolChangeListeners.add(t);
    }

    public void removeToolChangeListener(ToolChangeListener t) {
        toolChangeListeners.remove(t);
    }

    public interface ToolChangeListener {
        void onToolChange(String nextTool);
    }

    private void animatedDashPhase() {
        _dashPhase += _dashInc;
        if (_dashPhase >= 4.0f) _dashPhase = 0f;
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

    protected  void drawClear(Graphics g) {
        // Draw white background
        Point edge = new Point(screenSize.width, screenSize.height);
        edge = transformPoint(edge);

        g.setColor(canvasColor);
        g.fillRect(0, 0, edge.x, edge.y);
    }

    public Color getCanvasColor() {
        return canvasColor;
    }

    public void setCanvasColor(Color canvasColor) {
        this.canvasColor = canvasColor;
        repaint();
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
                (float) (1.0f / xScale),   // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_BEVEL,    // Join style
                1.0f,              // Miter limit
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

