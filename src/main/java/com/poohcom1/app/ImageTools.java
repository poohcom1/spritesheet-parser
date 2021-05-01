package com.poohcom1.app;

import com.poohcom1.app.imagetools.ImageToolsCanvas;
import com.poohcom1.app.reusables.CustomButton;
import com.poohcom1.app.reusables.ToggleButtonRadio;
import com.poohcom1.app.reusables.ZoomableScrollPane;
import com.poohcom1.spritesheetparser.image.ImageUtil;
import com.poohcom1.spritesheetparser.shapes2D.ShapesUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// =============================== Image Editing =====================================================
class ImageTools extends JPanel {
    private BufferedImage spriteSheet;

    // Components
    private final App app;

    final JFileChooser fileChooser;
    final JPanel mainPanel;
    final JPanel topPanel;
    final ToggleButtonRadio toolButtons;
    private final ZoomableScrollPane<ImageToolsCanvas> imageToolsPane;
    private final ImageToolsCanvas imageToolsCanvas;

    ImageTools(App app) {
        this.app = app;
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        imageToolsCanvas = new ImageToolsCanvas(ImageToolsCanvas.BLANK_CANVAS);
        imageToolsPane = new ZoomableScrollPane<>(imageToolsCanvas);


        mainPanel.add(imageToolsPane, BorderLayout.CENTER);

        JButton cropButton = new JButton("Crop Sprites!");

        // ======================== UPPER/LEFT IMAGE TOOLS PANEL ========================
        topPanel = new JPanel();
        toolButtons = new ToggleButtonRadio();
        toolButtons.setLayout(new BoxLayout(toolButtons, BoxLayout.PAGE_AXIS));

        JButton loadImage = new CustomButton("Load Spritesheet");

        toolButtons.removeAll();
        toolButtons.addButton(App.iconMap.get(App.ICON_MOVE), () -> imageToolsCanvas.setTool(ImageToolsCanvas.MOVE_TOOL), ImageToolsCanvas.MOVE_TOOL);
        toolButtons.addButton(App.iconMap.get(App.ICON_CROP), () -> imageToolsCanvas.setTool(ImageToolsCanvas.CROP_TOOL), ImageToolsCanvas.CROP_TOOL);
        toolButtons.addButton(App.iconMap.get(App.ICON_COLOR), () -> imageToolsCanvas.setTool(ImageToolsCanvas.COLOR_PICKER_TOOL), ImageToolsCanvas.COLOR_PICKER_TOOL);

        toolButtons.setButtonsEnabled(false);

        topPanel.add(loadImage);

        fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addChoosableFileFilter(new App.ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(true);

        loadImage.setFocusable(false);
        loadImage.addActionListener((e) -> {
            int fileOption = fileChooser.showOpenDialog(mainPanel);
            if (fileOption == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try {
                    spriteSheet = AppUtil.loadImage(file);

                    if (spriteSheet != null) {
                        System.out.println("Image loaded!");

                        imageToolsCanvas.setImage(spriteSheet);
                        repaint();

                        mainPanel.add(imageToolsPane, BorderLayout.CENTER);
                        mainPanel.revalidate();

                        app.packInBounds();

                        imageToolsPane.findMinZoom();

                        SwingUtilities.invokeLater(imageToolsPane::centerViewToPoint);

                        // todo: Fit screen to image on initial pack
                        cropButton.setEnabled(true);
                        toolButtons.setButtonsEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Invalid file type! Please select an image file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // ======================== LOWER CROP TOOLS PANEL ========================
        JPanel performEditPanel = new JPanel();
        performEditPanel.setLayout(new GridLayout(1, 3));
        performEditPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);

        cropButton.setFocusable(false);
        cropButton.setEnabled(false);
        cropButton.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // On extract sprites
        cropButton.addActionListener(l -> {
            if (imageToolsCanvas.crop().getHeight() > 300) {
                int result = JOptionPane.showOptionDialog(
                        mainPanel,
                        "This image is very large; are you sure you want to proceed without cropping?",
                        "Large file warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new String[]{"  Yes   ", "   No   "},
                        "   No   ");
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Crop sprites
            app.blobDetectionTools.init(imageToolsCanvas.crop(), imageToolsCanvas.getBackgroundColors());
            app.blobDetectionTools.mainPanel.repaint();
            app.tabbedPane.setSelectedIndex(App.SPRITE_EXTRACTION_PANE_TAB);
        });

        JLabel backgroundColorLabel = new JLabel("Background Color: ");
        JLabel backgroundColorBox = new JLabel(new ImageIcon(ShapesUtil.createColoredRectangle(10, 10, Color.WHITE)));

        imageToolsCanvas.addUpdateListener(() -> {
            backgroundColorBox.setIcon(new ImageIcon(ShapesUtil.createColoredRectangle(10, 10, ImageUtil.rgbaIntToColor(imageToolsCanvas.getBackgroundColors()[0]))));
        });


        JPanel backgroundColorPanel = new JPanel();
        backgroundColorPanel.setLayout(new BoxLayout(backgroundColorPanel, BoxLayout.LINE_AXIS));
        backgroundColorPanel.add(backgroundColorLabel);
        backgroundColorPanel.add(backgroundColorBox);

        JPanel cropButtonWrapper = new JPanel();
        cropButtonWrapper.add(cropButton);
        cropButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        cropButtonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));

        performEditPanel.add(new JPanel());
        performEditPanel.add(cropButtonWrapper);
        performEditPanel.add(backgroundColorPanel);
        performEditPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(toolButtons, BorderLayout.WEST);
        mainPanel.add(new JPanel(), BorderLayout.EAST);
        mainPanel.add(performEditPanel, BorderLayout.SOUTH);
    }
}
