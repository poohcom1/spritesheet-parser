package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.cv.BlobDetector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class Window  {
    private MyCanvas canvas;
    private int distance = 200;

    private BufferedImage image;
    private int[] backgroundColors;

    private boolean mousePressed = false;

    public Window(BufferedImage spriteSheet, int[] backgroundColors) {
        image = spriteSheet;
        this.backgroundColors = backgroundColors;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        canvas = new MyCanvas(spriteSheet, BlobDetector.detectBlobs(image, backgroundColors, distance));

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        JButton distanceUp = new JButton("Up");
        JButton distanceDown = new JButton("Down");

        distanceUp.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                incrementDistance(1);
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
                incrementDistance(-1);
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }

            public void mouseClicked(MouseEvent e) {

            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });


        panel.add(distanceUp);
        panel.add(distanceDown);

        mainPanel.add(canvas, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void incrementDistance(int value) {
        new Thread(() -> {
            while (mousePressed) {
                try {
                    if (!(distance + value <= 0 || distance + value > image.getHeight() || distance + value > image.getHeight())) {
                        distance += value;
                    }
                    Thread.sleep(10);
                    System.out.println(distance);
                    Rect[] blobs = BlobDetector.detectBlobs(image, backgroundColors, distance);
                    canvas.setBorders(blobs);
                    canvas.repaint();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }).start();
    }

}
