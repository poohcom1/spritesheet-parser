package com.poohcom1.spritesheetparser.app;

import com.poohcom1.spritesheetparser.app.blobdetection.BlobCanvas;
import com.poohcom1.spritesheetparser.app.reusables.ZoomableComponent;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.app.reusables.ZoomablePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {

    public App() throws IOException {
        JFrame window = new JFrame("Sprite Sheet Animator");

        BufferedImage image = AppUtil.loadImage("src/com/poohcom1/spritesheetparser/assets/tarma.png");

        window.add(new BlobDetectionTools(image).mainPanel);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}

// =============================== Blob Detection =====================================================

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
    private ZoomablePanel blobPanel;
    private BlobCanvas blobCanvas;
    JPanel mainPanel;

    public BlobDetectionTools(BufferedImage image) throws IOException {
        this.image = image;
        backgroundColors = ImageUtil.findBackgroundColor(image);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        // BLOB CANVAS
        blobCanvas = new BlobCanvas(image);
        blobPanel = new ZoomablePanel(blobCanvas);

        //updateCanvas();

        // BLOB
        mainPanel.add(blobPanel);
        mainPanel.add(setCanvasOptions());
        mainPanel.add(setBlobOptions());
    }

    private final int MOVE_TOOL = 0;
    private final int SELECT_TOOL = 1;
    private int activeTool = 0;

    private JPanel setCanvasOptions() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Image Editing"));

        List<JToggleButton> tools = new ArrayList<>();
        tools.add(new JToggleButton("Move"));
        tools.add(new JToggleButton("Select"));

        tools.get(0).setSelected(true);

        tools.forEach(button -> {
            button.setFocusable(false);
            button.addActionListener(e -> {
                tools.forEach(otherButton -> otherButton.setSelected(false));
                button.setSelected(true);
                activeTool = tools.indexOf(button);

                blobPanel.doMouseMove = false;
                blobCanvas.doDrawMarquee = false;

                switch (activeTool) {
                    case MOVE_TOOL -> {blobPanel.doMouseMove = true;}
                    case SELECT_TOOL -> {blobCanvas.doDrawMarquee = true;}
                }
            });
            optionsPanel.add(button);
        });

        return optionsPanel;
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