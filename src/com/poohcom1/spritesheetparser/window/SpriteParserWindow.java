package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.cv.*;
import com.poohcom1.spritesheetparser.util.sprite.SpriteSequence;
import com.poohcom1.spritesheetparser.window.blobdetection.BlobCanvas;
import com.poohcom1.spritesheetparser.window.spriteplayer.SpritePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteParserWindow {
    private BlobCanvas blobCanvas;
    private SpritePlayer spritePlayer;
    private int distance = 8;

    private final BufferedImage image;
    private final int[] backgroundColors;

    private JLabel distanceLabel;
    private JLabel blobCountLabel;
    private JLabel mousePosLabel;

    private List<Blob> blobs = new ArrayList<>();

    private int mouseClickTimer = 0;
    private final int MOUSE_HOLD_MAX = 10;
    private boolean mousePressed = false;

    public SpriteParserWindow(BufferedImage spriteSheet, int[] backgroundColors) {
        image = spriteSheet;
        this.backgroundColors = backgroundColors;

        JFrame frame = new JFrame();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        blobWindowSetup(mainPanel, spriteSheet);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);

        updateBlobs();
    }

    private void blobWindowSetup(JPanel mainPanel, BufferedImage spriteSheet) {
        blobCanvas = new BlobCanvas(spriteSheet);

        blobCanvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {

            }
            public void mouseMoved(MouseEvent e) {
                mousePosLabel.setText("(" + e.getX() + ", " + e.getY() + ")");
            }
        });

        blobCountLabel = new JLabel("Count: ");
        mousePosLabel = new JLabel();

        JPanel optionsPanel = new JPanel();

        JButton distanceUp = new JButton("Up");
        JButton distanceDown = new JButton("Down");
        distanceLabel = new JLabel(String.valueOf(distance));

        distanceUp.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                distanceButtonClickHandler(1);
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        distanceDown.addMouseListener(new MouseAdapter() {
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
        });

        JButton printBlobs = new JButton("Print");
        printBlobs.addActionListener((l) -> {
            for (int i = 0; i < blobs.size(); i++) {
                System.out.printf("#%d: %s\n", i+1, blobs.get(i).toString());
            }
        });

        JButton toggleBlobs = new JButton("Toggle");
        toggleBlobs.addActionListener((l) -> blobCanvas.toggleBlobs());

        optionsPanel.add(toggleBlobs);
        optionsPanel.add(printBlobs);
        optionsPanel.add(distanceUp);
        optionsPanel.add(distanceDown);
        optionsPanel.add(distanceLabel);
        optionsPanel.add(blobCountLabel);
        optionsPanel.add(mousePosLabel);

        mainPanel.add(blobCanvas, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);
    }

    private void playerWindowSetup(JPanel mainPanel) {
        SpriteSequence sprites = new SpriteSequence(image, (BlobSequence) blobs);

        spritePlayer = new SpritePlayer(sprites);
    }

    private void distanceButtonClickHandler(int value) {
        incrementDistance(value);
        updateBlobs();
        mouseClickTimer = 0;

        new Thread(() -> {
            while (mousePressed) {
                try {
                    if (mouseClickTimer < MOUSE_HOLD_MAX) {
                        mouseClickTimer++;
                    } else {
                        incrementDistance(value);
                        updateBlobs();
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

    private void updateBlobs() {
        blobs = new BlobSequence(image, backgroundColors, distance, BlobSequence.LEFT_TO_RIGHT, BlobSequence.TOP_TO_BOTTOM);

        Blob[] borders = blobs.toArray(new Blob[0]);
        Point[] points = BlobSequence.blobsToPoints(blobs);


        blobCountLabel.setText("Count: " + blobs.size());

        blobCanvas.setBlobs(borders);
        blobCanvas.setPoints(points);
        blobCanvas.repaint();
    }

    private void updateSpritePlayer() {

    }
}
