package com.poohcom1.spritesheetparser.app;

import com.poohcom1.spritesheetparser.app.animation.SpritePlayer;
import com.poohcom1.spritesheetparser.app.blobdetection.BlobCanvas;
import com.poohcom1.spritesheetparser.app.imagetools.ImageToolsCanvas;
import com.poohcom1.spritesheetparser.app.reusables.ToggleButtonRadio;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.app.reusables.ZoomableScrollPane;
import com.poohcom1.spritesheetparser.util.sprite.SpriteSequence;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App {
    private static JFrame window;
    private static JTabbedPane tabbedPane;

    private static int SPRITESHEET_EDITING_PANE = 0;
    private static int SPRITE_EXTRACTION_PANE = 1;

    private static BlobDetectionTools blobDetectionTools;

    public App() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        window = new JFrame("Sprite Sheet Animator");
        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        blobDetectionTools = new BlobDetectionTools(null);

        tabbedPane.addTab("Spritesheet Editing", new ImageTools().mainPanel);
        tabbedPane.addTab("Sprite Extraction", blobDetectionTools.mainPanel);

        tabbedPane.addChangeListener(l -> packInBounds());


        window.add(tabbedPane);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        new App();
    }


    /**
     * Courtesy of users/131872/camickr: https://stackoverflow.com/questions/40577930/java-set-maximum-size-of-jframe
     * Packs frame while making sure not to overlap with taskbar
     */
    private static void packInBounds() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        window.pack();
        window.revalidate();
        int width = Math.min(window.getWidth(), bounds.width);
        int height = Math.min(window.getHeight(), bounds.height);
        window.setSize( new Dimension(width, height) );
    }

// =============================== Image Editing =====================================================
    static class ImageTools {
        private BufferedImage spriteSheet;

        // Components
        final JFileChooser fileChooser = new JFileChooser();

        final JPanel mainPanel;
        final JPanel toolsPanel;

        private ZoomableScrollPane imageToolsPane;

        ImageTools() {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            imageToolsPane = new ZoomableScrollPane(new ImageToolsCanvas());

            // ======================== LOWER CROP TOOLS PANEL ========================
            JPanel performEditPanel = new JPanel();

            JButton confirmButton = new JButton("Extract Sprites!");
            confirmButton.setFocusable(false);
            confirmButton.setEnabled(false);
            confirmButton.addActionListener(l -> {
                blobDetectionTools.init(((ImageToolsCanvas)imageToolsPane.getChild()).crop());
                blobDetectionTools.mainPanel.repaint();
                tabbedPane.setSelectedIndex(SPRITE_EXTRACTION_PANE);
            });

            performEditPanel.add(confirmButton);

            // ======================== UPPER IMAGE TOOLS PANEL ========================
            toolsPanel = new JPanel();

            JButton loadImage = new JButton("Load Spritesheet");
            ToggleButtonRadio toolButtons = new ToggleButtonRadio();

            ImageToolsCanvas imageCanvas = ((ImageToolsCanvas) imageToolsPane.getChild());

            imageCanvas.getToolConstants().forEach(toolName ->
                    toolButtons.addButton(toolName, () -> imageCanvas.setTool(toolName))
            );


            toolButtons.setButtonsEnabled(false);

            toolsPanel.add(loadImage);
            toolsPanel.add(toolButtons);

            loadImage.setFocusable(false);
            loadImage.addActionListener((e) -> {
                fileChooser.addChoosableFileFilter(new ImageFilter());
                fileChooser.setAcceptAllFileFilterUsed(true);
                int fileOption = fileChooser.showOpenDialog(mainPanel);
                if (fileOption == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    try {
                        spriteSheet = AppUtil.loadImage(file);

                        if (spriteSheet != null) {
                            if (imageToolsPane != null) mainPanel.remove(imageToolsPane);

                            System.out.println("Image loaded!");

                            imageToolsPane = new ZoomableScrollPane(new ImageToolsCanvas(spriteSheet));

                            ImageToolsCanvas newImageCanvas = ((ImageToolsCanvas) imageToolsPane.getChild());

                            toolButtons.removeAll();

                            newImageCanvas.getToolConstants().forEach(toolName ->
                                    toolButtons.addButton(toolName, () -> newImageCanvas.setTool(toolName))
                            );


                            mainPanel.add(imageToolsPane, BorderLayout.CENTER);
                            mainPanel.revalidate();;

                            packInBounds();

                            confirmButton.setEnabled(true);
                            toolButtons.setButtonsEnabled(true);
                        } else {
                            JOptionPane.showMessageDialog(window, "Invalid file type! Please select an image file", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });

            mainPanel.add(toolsPanel, BorderLayout.NORTH);
            mainPanel.add(performEditPanel, BorderLayout.SOUTH);
        }
    }

    // Courtesy of https://www.tutorialspoint.com/swingexamples/show_file_chooser_images_only.htm
    static class ImageFilter extends FileFilter {
        public final static String JPEG = "jpeg";
        public final static String JPG = "jpg";
        public final static String GIF = "gif";
        public final static String TIFF = "tiff";
        public final static String TIF = "tif";
        public final static String PNG = "png";

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals(TIFF) ||
                        extension.equals(TIF) ||
                        extension.equals(GIF) ||
                        extension.equals(JPEG) ||
                        extension.equals(JPG) ||
                        extension.equals(PNG)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Image Only";
        }

        String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }
    }

    // ======================================= BLOB DETECTION =======================================
    static class BlobDetectionTools {
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
        private BlobSequence blobSequence;

        // Components
        ZoomableScrollPane<BlobCanvas> blobPanel;
        ZoomableScrollPane<SpritePlayer> spritePanel;

        SpritePlayer spritePlayer;

        JPanel mainPanel;

        public BlobDetectionTools(BufferedImage image) {
            this.image = image;

            mainPanel = new JPanel();

            if (image == null) {
                // Modals
                JTextField noImage = new JTextField("No sprites loaded");
                mainPanel.add(noImage);
                return;
            }
            init(image);
        }

        private void init(BufferedImage image) {
            this.image = image;

            mainPanel.removeAll();

            backgroundColors = ImageUtil.findBackgroundColor(image);

            mainPanel.setLayout(new BorderLayout());

            // Components
            blobPanel = new ZoomableScrollPane<>(new BlobCanvas(image));
            blobPanel.centerZoom();
            blobPanel.getChild().addUpdateListener(() ->
                    // Get the sprite player from the sprite panel
                    ((SpritePlayer) spritePanel.getChild()).setSprites(new SpriteSequence(image, blobSequence))
            );
            updateBlobs();

            spritePlayer = new SpritePlayer(new SpriteSequence(image, blobSequence), 160);

            spritePanel = new ZoomableScrollPane<>(spritePlayer);

            // BLOB
            mainPanel.add(blobPanel, BorderLayout.CENTER);
            mainPanel.add(setCanvasOptions(), BorderLayout.WEST);
            mainPanel.add(setBlobOptions(), BorderLayout.NORTH);
            mainPanel.add(spritePanel, BorderLayout.EAST);
            mainPanel.revalidate();
        }

        private JPanel setCanvasOptions() {
            ToggleButtonRadio optionsPanel = new ToggleButtonRadio();

            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

            BlobCanvas blobCanvas = blobPanel.getChild();

            blobCanvas.getToolConstants().forEach(toolName ->
                optionsPanel.addButton(toolName, () -> {
                    blobCanvas.setTool(toolName);
                })
            );

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
                int oldCount = blobSequence.size();
                do {
                    distanceThreshold++;
                    detectBlobs();
                } while (blobSequence.size() == oldCount);
                blobPanel.getChild().repaint();
            });

            down.addActionListener((e) -> {
                int oldCount = blobSequence.size();
                do {
                    if (distanceThreshold <= 2) break;
                    distanceThreshold--;
                    detectBlobs();
                } while (blobSequence.size() == oldCount);
                blobPanel.getChild().repaint();
            });

            JPanel panel = new JPanel();

            panel.add(new JLabel("Sprite Count:"));
            panel.add(up);
            panel.add(down);

            return panel;
        }

        private JPanel setBlobDirectionOption() {
            final String[] BLOB_DIRECTION = {"Horizontal", "Vertical", "Horizontal Reversed", "Vertical Reversed"};

            JComboBox<String> blobDirection = new JComboBox<>(BLOB_DIRECTION);

            blobDirection.addActionListener(actionEvent -> {
                switch (blobDirection.getSelectedIndex()) {
                    case 0 -> {
                        primaryOrder = BlobSequence.LEFT_TO_RIGHT;
                        secondaryOrder = BlobSequence.TOP_TO_BOTTOM;
                    }
                    case 1 -> {
                        primaryOrder = BlobSequence.TOP_TO_BOTTOM;
                        secondaryOrder = BlobSequence.LEFT_TO_RIGHT;
                    }case 2 -> {
                        primaryOrder = BlobSequence.RIGHT_TO_LEFT;
                        secondaryOrder = BlobSequence.TOP_TO_BOTTOM;
                    }
                    case 3 -> {
                        primaryOrder = BlobSequence.BOTTOM_TO_TOP;
                        secondaryOrder = BlobSequence.LEFT_TO_RIGHT;
                    }
                }
                updateBlobs();
            });

            JPanel panel = new JPanel();

            panel.add(new JLabel("Sprite Direction:"));
            panel.add(blobDirection);

            return panel;
        }

        private void detectBlobs() {
            blobSequence = new BlobSequence(image, backgroundColors, distanceThreshold, primaryOrder, secondaryOrder);
            BlobCanvas blobCanvas = (BlobCanvas) blobPanel.getChild();

            blobCanvas.setBlobs(blobSequence);
            blobCanvas.setShowBlobs(showBlobs);
            blobCanvas.setShowPoints(showPoints);
        }

        private void updateBlobs() {
            detectBlobs();
            BlobCanvas blobCanvas = (BlobCanvas) blobPanel.getChild();
            blobCanvas.repaint();
        }
    }
}