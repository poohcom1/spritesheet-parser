package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.cv.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlobWindow {
    private final BlobCanvas canvas;
    private int distance = 8;

    private final BufferedImage image;
    private int[] backgroundColors;

    private final JLabel distanceLabel;
    private final JLabel blobCountLabel;
    private final JLabel mousePosLabel;

    private ArrayList<Blob> blobs = new ArrayList<>();

    private int mouseClickTimer = 0;
    private final int MOUSE_HOLD_MAX = 10;
    private boolean mousePressed = false;

    public BlobWindow(BufferedImage spriteSheet, int[] backgroundColors) {
        image = spriteSheet;
        this.backgroundColors = backgroundColors;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        blobCountLabel = new JLabel("Count: ");
        mousePosLabel = new JLabel();

        canvas = new BlobCanvas(spriteSheet);

        canvas.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                System.out.println(e.getX() + ", " + e.getY());
            }
            public void mouseClicked(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {

            }
            public void mouseMoved(MouseEvent e) {
                mousePosLabel.setText("(" + e.getX() + ", " + e.getY() + ")");
            }
        });

        setCanvas();

        JFrame frame = new JFrame();
        JPanel optionsPanel = new JPanel();

        JButton distanceUp = new JButton("Up");
        JButton distanceDown = new JButton("Down");
        distanceLabel = new JLabel(String.valueOf(distance));

        distanceUp.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                distanceButtonClickHandler(1);
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }

            public void mouseClicked(MouseEvent e) {

            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });

        distanceDown.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                distanceButtonClickHandler(-1);
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }

            public void mouseClicked(MouseEvent e) {
                 mouseClickTimer = 0;
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });

        JButton printBlobs = new JButton("Print");
        printBlobs.addActionListener((l) -> {
            for (int i = 0; i < blobs.size(); i++) {
                System.out.printf("#%d: %s\n", i+1, blobs.get(i).toString());
            }
        });

        JButton toggleBlobs = new JButton("Toggle");
        toggleBlobs.addActionListener((l) -> canvas.toggleBlobs());

        optionsPanel.add(toggleBlobs);
        optionsPanel.add(printBlobs);
        optionsPanel.add(distanceUp);
        optionsPanel.add(distanceDown);
        optionsPanel.add(distanceLabel);
        optionsPanel.add(blobCountLabel);
        optionsPanel.add(mousePosLabel);

        mainPanel.add(canvas, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void distanceButtonClickHandler(int value) {
        incrementDistance(value);
        setCanvas();
        mouseClickTimer = 0;

        new Thread(() -> {
            while (mousePressed) {
                try {
                    if (mouseClickTimer < MOUSE_HOLD_MAX) {
                        mouseClickTimer++;
                    } else {
                        incrementDistance(value);
                        setCanvas();
                    }

                    Thread.sleep(70);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }).start();
    }

    private void incrementDistance(int value) {
        if (distance + value <= 1) {
            distance = 2;
        } else if (distance + value > Math.max(image.getHeight(), image.getWidth())) {
            distance = Math.max(image.getHeight(), image.getWidth());
        } else {
            distance += value;
        }
        distanceLabel.setText(String.valueOf(distance));
    }

    private void setCanvas() {
        blobs = BlobDetector.detectBlobs(image, backgroundColors, distance);
        BlobDetector.mergeBlobs(blobs);

        Rect[] borders = BlobDetector.blobsToRect(blobs);
        Point[] points = BlobDetector.blobsToPoints(blobs);


        blobCountLabel.setText("Count: " + blobs.size());

        canvas.setBorders(borders);
        canvas.setPoints(points);
        canvas.repaint();
    }

}
