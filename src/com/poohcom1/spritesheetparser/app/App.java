package com.poohcom1.spritesheetparser.app;

import com.poohcom1.spritesheetparser.app.blobdetection.BlobCanvas;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.app.reusables.ZoomablePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class App {
    private BlobCanvas blobCanvas;

    public App() throws IOException {
        JFrame window = new JFrame("Sprite Sheet Animator");

        BufferedImage image = AppUtil.loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet2.png");

        window.add(new BlobDetectionTools(image).mainPanel);

        window.pack();
        window.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}

class BlobDetectionTools {
    // Parameters
    private int[] backgroundColors;

    private int distanceThreshold = 2;
    private int primaryOrder = BlobSequence.LEFT_TO_RIGHT;
    private int secondaryOrder = BlobSequence.TOP_TO_BOTTOM;

    // Display parameters
    private boolean showBlobs = true;
    private boolean showNumbers = true;
    private boolean showPoints = false;

    // Objects
    private BufferedImage image;
    private BlobSequence blobs;

    // Components
    private BlobCanvas blobCanvas;
    JPanel mainPanel;

    public BlobDetectionTools(BufferedImage image) throws IOException {
        this.image = image;
        backgroundColors = ImageUtil.findBackgroundColor(image);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1));

        // BLOB CANVAS
        blobCanvas = new BlobCanvas(image);
        ZoomablePanel zoomBlobPanel = new ZoomablePanel(blobCanvas);

        updateCanvas();

        // BLOB
        mainPanel.add(zoomBlobPanel);
        mainPanel.add(setBlobOptions());
    }

    private JPanel setBlobOptions() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Sprite Detection"));

        optionsPanel.add(setDistanceButtons());
        optionsPanel.add(setBlobDirectionOption());

        return optionsPanel;
    }

    private JPanel setDistanceButtons() {
        JButton up = new JButton("-");
        JButton down = new JButton("+");

        up.addActionListener((e) -> {
            int oldCount = blobs.size();
            do {
                distanceThreshold++;
                detectBlobs();
            } while (blobs.size() == oldCount);
            blobCanvas.repaint();
        });

        down.addActionListener((e) -> {
            int oldCount = blobs.size();
            do {
                if (distanceThreshold <= 2) break;
                distanceThreshold--;
                detectBlobs();
            } while (blobs.size() == oldCount);
            blobCanvas.repaint();
        });

        JPanel panel = new JPanel();

        panel.add(new JLabel("Sprite Count:"));
        panel.add(up);
        panel.add(down);

        return panel;
    }

    private JPanel setBlobDirectionOption() {
        final String[] BLOB_DIRECTION = {"Horizontal Ordering", "Vertical Ordering"};

        JComboBox<String> blobDirection = new JComboBox<>(BLOB_DIRECTION);

        blobDirection.addActionListener(actionEvent -> {
            switch (blobDirection.getSelectedIndex()) {
                case 0 -> {primaryOrder = BlobSequence.LEFT_TO_RIGHT; secondaryOrder = BlobSequence.TOP_TO_BOTTOM;}
                case 1 -> {primaryOrder = BlobSequence.TOP_TO_BOTTOM; secondaryOrder = BlobSequence.LEFT_TO_RIGHT;}
            }
            updateCanvas();
        });

        JPanel panel = new JPanel();

        panel.add(new JLabel("Sprite Direction:"));
        panel.add(blobDirection);

        return panel;
    }

    private void detectBlobs() {
        blobs = new BlobSequence(image, backgroundColors, distanceThreshold, primaryOrder, secondaryOrder);
        blobCanvas.setBlobs(blobs);
        blobCanvas.setShowBlobs(showBlobs);
        blobCanvas.setShowPoints(showPoints);
    }

    private void updateCanvas() {
        detectBlobs();
        blobCanvas.repaint();
    }
}