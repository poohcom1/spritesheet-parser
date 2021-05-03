package com.poohcom1.app;

import com.poohcom1.app.animation.SpritePlayer;
import com.poohcom1.app.blobdetection.BlobCanvas;
import com.poohcom1.app.reusables.ToggleButtonRadio;
import com.poohcom1.app.reusables.ToolsCanvas;
import com.poohcom1.app.reusables.ZoomableScrollPane;
import com.poohcom1.spritesheetparser.cv.BlobSequence;
import com.poohcom1.spritesheetparser.image.ImageUtil;
import com.poohcom1.spritesheetparser.sprite.Sprite;
import com.poohcom1.spritesheetparser.sprite.SpriteSequence;
import com.poohcom1.spritesheetparser.sprite.SpriteUtil;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

// ======================================= BLOB DETECTION =======================================
class BlobDetectionTools {
    // Parameters
    private int[] backgroundColors;

    private int distanceThreshold = 2;
    private int primaryOrder = BlobSequence.LEFT_TO_RIGHT;
    private int secondaryOrder = BlobSequence.TOP_TO_BOTTOM;

    // Objects
    private BufferedImage image;
    private BlobSequence blobSequence;
    private SpriteSequence spriteSequence;

    // Components
    ZoomableScrollPane<BlobCanvas> blobPanel;
    ZoomableScrollPane<SpritePlayer> spritePanel;

    BlobCanvas blobCanvas;
    SpritePlayer spritePlayer;
    JPanel mainPanel;

    // Sprite player controls
    int fps = 12;

    // Fonts
    Font defaultFont = new Font("Arial", Font.PLAIN, 10);

    public BlobDetectionTools() {
        mainPanel = new JPanel();

        // Set empty
        mainPanel.add(new JLabel("No sprites loaded"));
    }

    void init(BufferedImage newImage, int[] backgroundColors) {
        mainPanel.removeAll();

        image = newImage;

        if (backgroundColors.length > 0) {
            this.backgroundColors = backgroundColors;
        } else {
            // If no background color was set in the Image Editing tab, automatically find the background color
            this.backgroundColors = ImageUtil.findBackgroundColor(newImage);
        }

        // Finds the background color, and replaces it with a new color
        image = ImageUtil.replaceColors(image, this.backgroundColors, new Color(0, 0, 0, 0).getRGB());
        this.backgroundColors = new int[]{new Color(0, 0, 0, 0).getRGB()};

        mainPanel.setLayout(new BorderLayout());

        // Components
        blobCanvas = new BlobCanvas(image);

        blobPanel = new ZoomableScrollPane<>(blobCanvas);
        blobCanvas.addUpdateListener(this::resetSpritePlayer);
        updateBlobs();

        SwingUtilities.invokeLater(blobPanel::centerViewToPoint);

        // Sprite player
        JPanel spritePlayerPanel = new JPanel();
        spritePlayerPanel.setLayout(new BoxLayout(spritePlayerPanel, BoxLayout.PAGE_AXIS));

        spriteSequence = new SpriteSequence(image, blobSequence, this.backgroundColors[0]);

        spritePlayer = new SpritePlayer(spriteSequence, (long) SpriteUtil.MsFromFps(fps));
        spritePanel = new ZoomableScrollPane<>(spritePlayer);

        spritePlayerPanel.add(spritePanel);
        spritePlayerPanel.add(setSpritePLayerOptions());

        spritePlayerPanel.setBorder(BorderFactory.createTitledBorder("Sprite Player"));

        // BLOB
        mainPanel.add(blobPanel, BorderLayout.CENTER);
        mainPanel.add(setCanvasPanel(), BorderLayout.WEST);
        mainPanel.add(setTopPanel(), BorderLayout.NORTH);
        mainPanel.add(spritePlayerPanel, BorderLayout.EAST);
        mainPanel.add(setBottomPanel(), BorderLayout.SOUTH);
        mainPanel.revalidate();
    }

    // =========================== CANVAS NAVIGATION/EDITING TOOLS
    private JPanel setCanvasPanel() {
        ToggleButtonRadio optionsPanel = new ToggleButtonRadio();

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

        optionsPanel.addButton(App.iconMap.get(App.ICON_MOVE), () -> blobCanvas.setTool(ToolsCanvas.MOVE_TOOL), ToolsCanvas.MOVE_TOOL);
        optionsPanel.addButton(App.iconMap.get(App.ICON_MERGE), () -> blobCanvas.setTool(BlobCanvas.MERGE_TOOL), BlobCanvas.MERGE_TOOL);
        optionsPanel.addButton(App.iconMap.get(App.ICON_DELETE), () -> blobCanvas.setTool(BlobCanvas.DELETE_TOOL), BlobCanvas.DELETE_TOOL);
        optionsPanel.addButton(App.iconMap.get(App.ICON_CUT), () -> blobCanvas.setTool(BlobCanvas.CUT_TOOL), BlobCanvas.CUT_TOOL);

        return optionsPanel;
    }

    // ======================= TOP MENU BAR OPTIONS
    private JPanel setTopPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.LINE_AXIS));

        optionsPanel.add(setDistanceButtons());
        optionsPanel.add(setBlobDirectionOption());
        optionsPanel.add(setSpriteAlignmentOptions());
        optionsPanel.add(setExportOptions());

        return optionsPanel;
    }

    private JPanel setDistanceButtons() {
        JButton up = new JButton(App.iconMap.get(App.ICON_MINUS));
        JButton down = new JButton(App.iconMap.get(App.ICON_PLUS));
        JLabel countLabel = new JLabel(String.valueOf(blobSequence.size()));

        JLabel warningLabel = new JLabel(App.iconMap.get(App.ICON_EDIT_WARNING));
        warningLabel.setToolTipText(App.ICON_EDIT_WARNING);
        warningLabel.setVisible(false);

        Font font = countLabel.getFont();

        up.setFocusable(false);

        up.addActionListener((e) -> {
            warningLabel.setVisible(false);
            countLabel.setFont(font.deriveFont(font.getStyle() | Font.PLAIN));
            int oldCount = blobSequence.size();

            if (blobSequence.size() <= 2) {
                countLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
                return;
            }

            do {
                distanceThreshold++;
                detectBlobs();
                if (blobSequence.size() <= 2) {
                    countLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
                    return;
                }
            } while (blobSequence.size() == oldCount);

            countLabel.setText(String.valueOf(blobSequence.size()));

            blobCanvas.repaint();
            blobPanel.revalidate();
            resetSpritePlayer();
        });

        down.setFocusable(false);
        down.addActionListener((e) -> {
            warningLabel.setVisible(false);
            countLabel.setFont(font.deriveFont(font.getStyle() | Font.PLAIN));
            int oldCount = blobSequence.size();
            do {
                if (distanceThreshold <= 2) break;
                System.out.println(distanceThreshold);
                distanceThreshold--;
                detectBlobs();
            } while (blobSequence.size() == oldCount);

            countLabel.setText(String.valueOf(blobSequence.size()));
            if (blobSequence.size() == oldCount) countLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

            blobCanvas.repaint();

            blobPanel.revalidate();
            resetSpritePlayer();
        });

        // Show warning after edit to blob canvas
        blobCanvas.addUpdateListener(() -> warningLabel.setVisible(true));

        JPanel panel = new JPanel();

        panel.add(new JLabel("Sprite Count:"));
        panel.add(up);
        panel.add(countLabel);
        panel.add(down);
        panel.add(warningLabel);
        panel.setBorder(BorderFactory.createTitledBorder("Re-detect Sprites"));

        return panel;
    }

    private JPanel setBlobDirectionOption() {
        final String[] BLOB_DIRECTION = {"Horizontal", "Vertical", "Horizontal Reversed", "Vertical Reversed"};

        JComboBox<String> blobDirection = new JComboBox<>(BLOB_DIRECTION);
        blobDirection.setFont(defaultFont);
        blobDirection.setFocusable(false);

        blobDirection.addActionListener(actionEvent -> {
            switch (blobDirection.getSelectedIndex()) {
                case 0 -> {
                    primaryOrder = BlobSequence.LEFT_TO_RIGHT;
                    secondaryOrder = BlobSequence.TOP_TO_BOTTOM;
                }
                case 1 -> {
                    primaryOrder = BlobSequence.TOP_TO_BOTTOM;
                    secondaryOrder = BlobSequence.LEFT_TO_RIGHT;
                }
                case 2 -> {
                    primaryOrder = BlobSequence.RIGHT_TO_LEFT;
                    secondaryOrder = BlobSequence.BOTTOM_TO_TOP;
                }
                case 3 -> {
                    primaryOrder = BlobSequence.BOTTOM_TO_TOP;
                    secondaryOrder = BlobSequence.RIGHT_TO_LEFT;
                }
            }

            blobSequence.setOrder(primaryOrder, secondaryOrder);
            blobSequence.orderBlobs();
            blobCanvas.repaint();
            resetSpritePlayer();
        });

        JPanel panel = new JPanel();

        panel.add(blobDirection);
        panel.setBorder(BorderFactory.createTitledBorder("Sprite Direction"));

        return panel;
    }

    private JPanel setSpriteAlignmentOptions() {
        JButton left = new JButton(App.iconMap.get(App.ICON_H_ALIGN_LEFT));
        JButton centerH = new JButton(App.iconMap.get(App.ICON_H_ALIGN_CENTER));
        JButton right = new JButton(App.iconMap.get(App.ICON_H_ALIGN_RIGHT));
        JButton top = new JButton(App.iconMap.get(App.ICON_V_ALIGN_TOP));
        JButton centerV = new JButton(App.iconMap.get(App.ICON_V_ALIGN_CENTER));
        JButton bottom = new JButton(App.iconMap.get(App.ICON_V_ALIGN_BOTTOM));

        left.addActionListener(l -> spriteSequence.alignSprites(Sprite.LEFT_ALIGN));
        centerH.addActionListener(l -> spriteSequence.alignSprites(Sprite.CENTER_ALIGN_X));
        right.addActionListener(l -> spriteSequence.alignSprites(Sprite.RIGHT_ALIGN));
        top.addActionListener(l -> spriteSequence.alignSprites(Sprite.TOP_ALIGN));
        centerV.addActionListener(l -> spriteSequence.alignSprites(Sprite.CENTER_ALIGN_Y));
        bottom.addActionListener(l -> spriteSequence.alignSprites(Sprite.BOTTOM_ALIGN));

        JPanel hAlignPanel = new JPanel();
        //hAlignPanel.setBorder(new TitledBorder("Horizontal Alignment"));
        JPanel vAlignPanel = new JPanel();
        //vAlignPanel.setBorder(new TitledBorder("Vertical Alignment"));

        hAlignPanel.add(left);
        hAlignPanel.add(centerH);
        hAlignPanel.add(right);

        vAlignPanel.add(top);
        vAlignPanel.add(centerV);
        vAlignPanel.add(bottom);

        for (Component c : hAlignPanel.getComponents()) {
            c.setFocusable(false);
        }

        for (Component c : vAlignPanel.getComponents()) {
            c.setFocusable(false);
        }

        JPanel options = new JPanel();
        options.setBorder(BorderFactory.createTitledBorder("Sprite Alignment"));
        options.add(hAlignPanel);
        options.add(vAlignPanel);
        return options;
    }

    private JFileChooser fileChooser;

    private JPanel setExportOptions() {
        JPanel panel = new JPanel();

        JButton exportButton = new JButton("Export sprites...");

        fileChooser = new JFileChooser() {
            // Courtesy of /users/190165/roberto-luis-bisbé: https://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {

                    int result = JOptionPane.showConfirmDialog(this, "Overwrite existing files?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }
        };

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(App.getImageExtensions(App.PNG));
        fileChooser.addChoosableFileFilter(App.getImageExtensions(App.JPEG));
        fileChooser.addChoosableFileFilter(App.getImageExtensions(App.JPG));
        fileChooser.addChoosableFileFilter(App.getImageExtensions(App.GIF));

        exportButton.setFocusable(false);
        exportButton.addActionListener((e) -> {
            List<BufferedImage> images = spriteSequence.getImages();

            int fileOption = fileChooser.showSaveDialog(mainPanel);
            if (fileOption == JFileChooser.APPROVE_OPTION) {
                File savedFile = fileChooser.getSelectedFile();

                String name = savedFile.getName();
                String dir = fileChooser.getCurrentDirectory().getPath();
                String format = fileChooser.getFileFilter().getDescription();

                if (name.toLowerCase().contains(format.toLowerCase())) {
                    String[] splitName = name.split("\\.");
                    if (splitName[splitName.length - 1].equalsIgnoreCase(format)) {
                        name = name.substring(0, name.length() - format.length() - 1);
                    }
                }

                if (name.charAt(name.length() - 1) == '0') {
                    name = name.substring(0, name.length() - 1);
                }

                System.out.println(name);

                try {
                    for (int i = 0; i < images.size(); i++) {
                        AppUtil.saveImage(images.get(i), dir, name + i + "." + format.toLowerCase(), format);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        panel.add(exportButton);
        return panel;
    }

    // ================================== BOTTOM MENU BAR
    private JPanel setBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox showBlobs = new JCheckBox("Show sprite boxes");
        JCheckBox showNumbers = new JCheckBox("Show sprite number");
        JCheckBox showPoints = new JCheckBox("Show sprite pixels");

        showBlobs.setSelected(blobCanvas.isShowingBlobs());
        showNumbers.setSelected(blobCanvas.isShowingNumbers());
        showPoints.setSelected(blobCanvas.isShowingPoints());

        showBlobs.addActionListener(l -> blobCanvas.setShowBlobs(showBlobs.isSelected()));
        showNumbers.addActionListener(l -> blobCanvas.setShowNumbers(showNumbers.isSelected()));
        showPoints.addActionListener(l -> blobCanvas.setShowPoints(showPoints.isSelected()));

        panel.add(showBlobs);
        panel.add(showNumbers);
        panel.add(showPoints);

        // Set color picker
        JColorChooser colorChooser = new JColorChooser();
        AbstractColorChooserPanel[] defaultPanels = colorChooser.getChooserPanels();
        colorChooser.removeChooserPanel(defaultPanels[1]);
        colorChooser.removeChooserPanel(defaultPanels[2]);
        colorChooser.removeChooserPanel(defaultPanels[3]);  // HSL
        colorChooser.removeChooserPanel(defaultPanels[4]); // CMYK
        colorChooser.setPreviewPanel(new JPanel());

        colorChooser.getSelectionModel().addChangeListener(l -> blobCanvas.setCanvasColor(colorChooser.getColor()));

        JButton pickBackgroundColor = new JButton("Pick background color");
        pickBackgroundColor.addActionListener(l -> {
            Color originalColor = blobCanvas.getCanvasColor();
            colorChooser.setColor(originalColor);
            JDialog colorChooserDialog = JColorChooser.createDialog(mainPanel, "Pick color", false, colorChooser,
                    null,
                    c -> blobCanvas.setCanvasColor(originalColor)
            );
            colorChooserDialog.setVisible(true);
        });

        panel.add(pickBackgroundColor);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 100, 5, 0));
        return panel;
    }

    // =================================== SPRITE PLAYER PANELS
    private JPanel setSpritePLayerOptions() {
        JPanel options = new JPanel();

        JToggleButton playButton = new JToggleButton(App.iconMap.get(App.ICON_PAUSE));
        playButton.addActionListener(l -> {
            if (spritePanel.getChild().isPlaying()) {
                playButton.setIcon(App.iconMap.get(App.ICON_PLAY));
                spritePanel.getChild().pause();
            } else {
                playButton.setIcon(App.iconMap.get(App.ICON_PAUSE));
                spritePanel.getChild().play();
            }
        });

        JButton fpsUp = new JButton("+");
        JButton fpsDown = new JButton("-");

        JLabel fpsLabel = new JLabel(String.format("%3d", fps));
        fpsUp.addActionListener(l -> {
            fps++;
            spritePlayer.setMsPerFrame((long) SpriteUtil.MsFromFps(fps));
            resetSpritePlayer();
            fpsLabel.setText(String.format("%3d", fps));
        });
        fpsDown.addActionListener(l -> {
            if (fps > 1) fps--;
            resetSpritePlayer();
            spritePlayer.setMsPerFrame((long) SpriteUtil.MsFromFps(fps));
            fpsLabel.setText(String.format("%2d", fps));
        });

        options.add(playButton);
        options.add(new JLabel("FPS: "));
        options.add(fpsDown);
        options.add(fpsLabel);
        options.add(fpsUp);

        for (Component c : options.getComponents()) {
            c.setFocusable(false);
        }

        return options;
    }

    private void detectBlobs() {
        blobSequence = new BlobSequence(image, backgroundColors, distanceThreshold, primaryOrder, secondaryOrder);
        blobCanvas.setBlobs(blobSequence);
    }

    private void updateBlobs() {
        detectBlobs();
        blobCanvas.repaint();
    }

    private void resetSpritePlayer() {
        // Get the sprite player from the sprite panel
        spriteSequence = new SpriteSequence(image, blobSequence, backgroundColors[0]);
        spritePanel.getChild().setSprites(spriteSequence);
    }
}
