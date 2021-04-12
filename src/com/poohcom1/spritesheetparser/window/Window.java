package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.cv.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window  {
    private final MyCanvas canvas;
    private int distance = 10;

    private final BufferedImage image;
    private int[] backgroundColors;

    private final JLabel distanceLabel;

    private int mouseClickTimer = 0;
    private final int MOUSE_HOLD_MAX = 50;
    private boolean mousePressed = false;

    public Window(BufferedImage spriteSheet, int[] backgroundColors) {
        image = spriteSheet;
        this.backgroundColors = backgroundColors;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        canvas = new MyCanvas(spriteSheet);

        setCanvas();

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

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


        panel.add(distanceUp);
        panel.add(distanceDown);
        panel.add(distanceLabel);

        mainPanel.add(canvas, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void distanceButtonClickHandler(int value) {
        incrementDistance(value);
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

                    Thread.sleep(10);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }).start();
    }

    private void incrementDistance(int value) {
        if (distance + value <= 0) {
            distance = 1;
        } else if (distance + value > Math.max(image.getHeight(), image.getWidth())) {
            distance = Math.max(image.getHeight(), image.getWidth());
        } else {
            distance += value;
        }
        distanceLabel.setText(String.valueOf(distance));
    }

    private void setCanvas() {
        ArrayList<Blob> blobs = BlobDetector.detectBlobs(image, backgroundColors, distance);
        Rect[] borders = BlobDetector.blobsToRect(blobs);
        Point[] points = BlobDetector.blobsToPoints(blobs);

        System.out.println(blobs.size());

        canvas.setBorders(borders);
        canvas.setPoints(points);
        canvas.repaint();
    }

}
