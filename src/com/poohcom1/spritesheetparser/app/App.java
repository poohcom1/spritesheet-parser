package com.poohcom1.spritesheetparser.app;

import com.poohcom1.spritesheetparser.app.animation.SpritePlayer;
import com.poohcom1.spritesheetparser.app.blobdetection.BlobCanvas;
import com.poohcom1.spritesheetparser.app.imagetools.ImageToolsCanvas;
import com.poohcom1.spritesheetparser.app.reusables.ToggleButtonRadio;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.app.reusables.ZoomableScrollPane;
import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteSequence;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.swing.*;
import org.kordamp.ikonli.unicons.*;

public class App {
    // Icons
    static Map<String, FontIcon> iconMap;

    // Components
    private static JFrame window;
    private static JTabbedPane tabbedPane;

    // Tabs
    private static final int SHEET_EDITING_PANE = 0;
    private static final int SPRITE_EXTRACTION_PANE = 1;

    private static BlobDetectionTools blobDetectionTools;

    public App() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        prepareIcons();

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
        window.setSize(new Dimension(width, height));
    }

    private final static String ICON_CURSOR = "Move";
    private final static String ICON_CROP = "Crop";

    private final static String ICON_EDIT_WARNING = "Warning: Re-detecting sprites will override your edits!";

    private final static String ICON_H_ALIGN_CENTER = "Align center horizontally";
    private final static String ICON_H_ALIGN_LEFT = "Align left";
    private final static String ICON_H_ALIGN_RIGHT = "Align right";
    private final static String ICON_V_ALIGN_CENTER = "Align center vertically";
    private final static String ICON_V_ALIGN_TOP = "Align top";
    private final static String ICON_V_ALIGN_BOTTOM = "Align bottom";

    private final static String ICON_PLAY = "Play";
    private final static String ICON_PAUSE = "Pause";

    static void prepareIcons() {
        iconMap = new HashMap<>();
        iconMap.put(ICON_H_ALIGN_CENTER, FontIcon.of(UniconsLine.HORIZONTAL_ALIGN_CENTER));
        iconMap.put(ICON_H_ALIGN_LEFT, FontIcon.of(UniconsLine.HORIZONTAL_ALIGN_LEFT));
        iconMap.put(ICON_H_ALIGN_RIGHT, FontIcon.of(UniconsLine.HORIZONTAL_ALIGN_RIGHT));
        iconMap.put(ICON_V_ALIGN_CENTER, FontIcon.of(UniconsLine.VERTICAL_ALIGN_CENTER));
        iconMap.put(ICON_V_ALIGN_TOP, FontIcon.of(UniconsLine.VERTICAL_ALIGN_TOP));
        iconMap.put(ICON_V_ALIGN_BOTTOM, FontIcon.of(UniconsLine.VERTICAL_ALIGN_BOTTOM));

        iconMap.put(ICON_EDIT_WARNING, FontIcon.of(BoxiconsSolid.ERROR));

        iconMap.values().forEach(icon -> icon.setIconSize(20));



        iconMap.put(ICON_PLAY, FontIcon.of(UniconsLine.PLAY));
        iconMap.put(ICON_PAUSE, FontIcon.of(UniconsLine.PAUSE));
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
                blobDetectionTools.init(((ImageToolsCanvas) imageToolsPane.getChild()).crop());
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
                            mainPanel.revalidate();
                            ;

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
        private SpriteSequence spriteSequence;

        // Components
        ZoomableScrollPane<BlobCanvas> blobPanel;
        ZoomableScrollPane<SpritePlayer> spritePanel;

        SpritePlayer spritePlayer;
        JPanel mainPanel;

        // Sprite player controls
        int fps = 12;

        // Fonts
        Font defaultFont = new Font("Arial", Font.PLAIN, 10);

        // Editing
        private boolean blobsEdited = false;

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
            blobPanel.getChild().addUpdateListener(this::resetSpritePlayer);
            updateBlobs();

            // Sprite player
            JPanel spritePlayerPanel = new JPanel();
            spritePlayerPanel.setLayout(new BoxLayout(spritePlayerPanel, BoxLayout.PAGE_AXIS));

            spriteSequence = new SpriteSequence(image, blobSequence);

            spritePlayer = new SpritePlayer(spriteSequence, (long) SpriteUtil.MsFromFps(fps));
            spritePanel = new ZoomableScrollPane<>(spritePlayer);

            spritePlayerPanel.add(spritePanel);
            spritePlayerPanel.add(setSpritePLayerOptions());

            spritePlayerPanel.setBorder(BorderFactory.createTitledBorder("Sprite Player"));

            // BLOB
            mainPanel.add(blobPanel, BorderLayout.CENTER);
            mainPanel.add(setCanvasOptions(), BorderLayout.WEST);
            mainPanel.add(setTopMenuBar(), BorderLayout.NORTH);
            mainPanel.add(spritePlayerPanel, BorderLayout.EAST);
            mainPanel.revalidate();
        }

        // =========================== CANVAS NAVIGATION/EDITING TOOLS
        private JPanel setCanvasOptions() {
            ToggleButtonRadio optionsPanel = new ToggleButtonRadio();

            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

            BlobCanvas blobCanvas = blobPanel.getChild();

            blobCanvas.getToolConstants().forEach(toolName ->
                    optionsPanel.addButton(toolName, () -> blobCanvas.setTool(toolName))
            );

            return optionsPanel;
        }

        // ======================= TOP MENU BAR OPTIONS
        private JPanel setTopMenuBar() {
            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.LINE_AXIS));

            optionsPanel.add(setDistanceButtons());
            optionsPanel.add(setBlobDirectionOption());
            optionsPanel.add(setSpriteAlignmentOptions());

            return optionsPanel;
        }

        private JPanel setDistanceButtons() {
            JButton up = new JButton("-");
            JButton down = new JButton("+");
            JLabel countLabel = new JLabel(String.valueOf(blobSequence.size()));

            JLabel warningLabel = new JLabel(iconMap.get(ICON_EDIT_WARNING));
            warningLabel.setToolTipText(ICON_EDIT_WARNING);
            warningLabel.setVisible(false);

            Font font = countLabel.getFont();

            up.setFocusable(false);
            up.addActionListener((e) -> {
                warningLabel.setVisible(false);
                countLabel.setFont(font.deriveFont(font.getStyle() | Font.PLAIN));
                int oldCount = blobSequence.size();

                if (blobSequence.size() == 2) {
                    countLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
                    return;
                }

                do {
                    distanceThreshold++;
                    detectBlobs();
                } while (blobSequence.size() == oldCount);

                countLabel.setText(String.valueOf(blobSequence.size()));

                blobPanel.getChild().repaint();
                resetSpritePlayer();
            });

            down.setFocusable(false);
            down.addActionListener((e) -> {
                warningLabel.setVisible(false);
                countLabel.setFont(font.deriveFont(font.getStyle() | Font.PLAIN));
                int oldCount = blobSequence.size();
                do {
                    if (distanceThreshold <= 2) break;
                    distanceThreshold--;
                    detectBlobs();
                } while (blobSequence.size() == oldCount);

                countLabel.setText(String.valueOf(blobSequence.size()));
                if (blobSequence.size() == oldCount) countLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

                blobPanel.getChild().repaint();
                resetSpritePlayer();
            });

            // Show warning after edit to blob canvas
            blobPanel.getChild().addUpdateListener(() -> warningLabel.setVisible(true));

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
                blobSequence.orderBlobs();
                blobPanel.getChild().repaint();
                resetSpritePlayer();
            });

            JPanel panel = new JPanel();

            panel.add(blobDirection);
            panel.setBorder(BorderFactory.createTitledBorder("Sprite Direction"));

            return panel;
        }

        private JPanel setSpriteAlignmentOptions() {
            JButton left = new JButton(iconMap.get(ICON_H_ALIGN_LEFT));
            JButton centerH = new JButton(iconMap.get(ICON_H_ALIGN_CENTER));
            JButton right = new JButton(iconMap.get(ICON_H_ALIGN_RIGHT));
            JButton top = new JButton(iconMap.get(ICON_V_ALIGN_TOP));
            JButton centerV = new JButton(iconMap.get(ICON_V_ALIGN_CENTER));
            JButton bottom = new JButton(iconMap.get(ICON_V_ALIGN_BOTTOM));

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

        private JPanel setSpritePLayerOptions() {
            JPanel options = new JPanel();

            JToggleButton playButton = new JToggleButton(iconMap.get(ICON_PAUSE));
            playButton.addActionListener(l -> {
                if (spritePanel.getChild().isPlaying()) {
                    playButton.setIcon(iconMap.get(ICON_PLAY));
                    spritePanel.getChild().pause();
                } else {
                    playButton.setIcon(iconMap.get(ICON_PAUSE));
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
            BlobCanvas blobCanvas = blobPanel.getChild();

            blobCanvas.setBlobs(blobSequence);
            blobCanvas.setShowBlobs(showBlobs);
            blobCanvas.setShowPoints(showPoints);
        }

        private void updateBlobs() {
            detectBlobs();
            BlobCanvas blobCanvas = blobPanel.getChild();
            blobCanvas.repaint();
        }

        private void resetSpritePlayer() {
            // Get the sprite player from the sprite panel
            spriteSequence = new SpriteSequence(image, blobSequence);
            spritePanel.getChild().setSprites(spriteSequence);
            //System.out.println(blobSequence);
            System.out.println(blobSequence.getRow(0).size() + ":" + spriteSequence.size());
        }
    }
}