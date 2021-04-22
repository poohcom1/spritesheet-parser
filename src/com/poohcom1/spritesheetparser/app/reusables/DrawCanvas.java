package com.poohcom1.spritesheetparser.app.reusables;

import com.poohcom1.spritesheetparser.app.reusables.ZoomableComponent;
import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.shapes2D.Point;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class DrawCanvas extends ZoomableComponent {
    // Tools
    public static final int MOVE_TOOL = 0;
    public static final int MARQUEE_TOOL = 1;
    public static final int PEN_TOOL = 2;

    public int toolIndex = 0;

    // Objects
    private List<Rect> marquees;
    private List<Point> penPoints;

    public DrawCanvas(int width, int height) {
        super(width, height);

        marquees = new ArrayList<>();
        penPoints = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point2D mousePos = transformedMousePos(e);

                parentPanel.setMouseZoom(false);
                parentPanel.setMouseMove(false);

                switch (toolIndex) {
                    case MOVE_TOOL -> {
                        parentPanel.setMouseMove(true);
                        parentPanel.setMouseZoom(true);
                    }
                    case MARQUEE_TOOL -> {
                        if (e.getButton() == MouseEvent.BUTTON1 && !parentPanel.panKeyPressed()) {
                            startMarquee((int) (mousePos.getX()), (int) (mousePos.getY()));
                            repaint();
                        }
                    }
                    case PEN_TOOL -> {}
                }


            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point2D mousePos = transformedMousePos(e);

                switch (toolIndex) {
                    case MARQUEE_TOOL -> {
                        if (parentPanel.m1Pressed() && !parentPanel.panKeyPressed()) {
                            drawMarquee((int) (mousePos.getX()), (int) (mousePos.getY()));
                            repaint();
                        }
                    }
                }
            }
        });
    }

    public void startMarquee(int x, int y) {
        Rect newMarquee = new Rect(x, y, x, y);
        newMarquee.setAnchor();
        marquees.add(newMarquee);
    }

    public void drawMarquee(int x, int y) {
        if (marquees.isEmpty()) return;
        marquees.get(marquees.size()-1).resizeWithAnchor(x, y);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    protected void drawMarquees(Graphics g) {
        ((Graphics2D)g).setStroke(new BasicStroke(
                1.0f,                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] {3.0f,2.0f},          // Dash pattern
                0.0f));

        g.setColor(Color.BLACK);
        marquees.forEach(marquee -> g.drawRect(marquee.x, marquee.y, marquee.width, marquee.height));
    }
}
