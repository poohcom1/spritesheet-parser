package com.poohcom1.spritesheetparser.app;

import com.poohcom1.spritesheetparser.app.blobdetection.BlobCanvas;
import com.poohcom1.spritesheetparser.app.imagetools.ImageToolsCanvas;
import com.poohcom1.spritesheetparser.app.reusables.ImageCanvas;
import com.poohcom1.spritesheetparser.app.reusables.ToggleButtonRadio;
import com.poohcom1.spritesheetparser.app.reusables.ZoomableComponent;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.app.reusables.ZoomablePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {

    public App() throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        JFrame window = new JFrame("Sprite Sheet Animator");
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        BufferedImage image = AppUtil.loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png");

        tabbedPane.addTab("Spritesheet Editing", new ImageTools().mainPanel);
        tabbedPane.addTab("Sprite Extraction", new BlobDetectionTools(image).mainPanel);
        window.add(tabbedPane);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        new App();
    }
}

// =============================== Blob Detection =====================================================

class ImageTools {
    private int[] backgroundColors;
    private BufferedImage spriteSheet;

    // Components
    final JFileChooser fileChooser = new JFileChooser();

    final JPanel mainPanel;
    final JPanel toolsPanel;

    private ZoomablePanel imageToolsPane;

    ImageTools() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        toolsPanel = new JPanel();

        JButton loadImage = new JButton("Load Spritesheet");
        ToggleButtonRadio toolButtons = new ToggleButtonRadio();
        toolButtons.addButton("Move");
        toolButtons.addButton("Select");

        toolButtons.setButtonsEnabled(false);

        toolsPanel.add(loadImage);
        toolsPanel.add(toolButtons);

        toolButtons.addButtonToggledListener(i -> ((ImageCanvas)imageToolsPane.getChild()).setTool(i));

        loadImage.setFocusable(false);
        loadImage.addActionListener((e) -> {
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(true);
            int fileOption = fileChooser.showOpenDialog(mainPanel);
            if(fileOption == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();

                try {
                    spriteSheet = AppUtil.loadImage(file);

                    if (spriteSheet != null) {
                        if (imageToolsPane != null) mainPanel.remove(imageToolsPane);
                        imageToolsPane = new ZoomablePanel(new ImageToolsCanvas(spriteSheet));
                        mainPanel.add(imageToolsPane, BorderLayout.CENTER);

                        toolButtons.setButtonsEnabled(true);
                    } else {
                        //TODO: Add error dialog box
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        mainPanel.add(toolsPanel, BorderLayout.NORTH);
    }

}

// Courtesy of https://www.tutorialspoint.com/swingexamples/show_file_chooser_images_only.htm
class ImageFilter extends FileFilter {
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

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
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

    ZoomablePanel blobPanel;

    final JPanel mainPanel;

    public BlobDetectionTools(BufferedImage image) {
        this.image = image;
        backgroundColors = ImageUtil.findBackgroundColor(image);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        // Components
        blobPanel = new ZoomablePanel(new BlobCanvas(image));
        //updateCanvas();

        // BLOB
        mainPanel.add(blobPanel);
        mainPanel.add(setCanvasOptions());
        mainPanel.add(setBlobOptions());
    }


    private JPanel setCanvasOptions() {
        ToggleButtonRadio optionsPanel = new ToggleButtonRadio();

        optionsPanel.addButton("Move");
        optionsPanel.addButton("Select");

        optionsPanel.addButtonToggledListener(i -> ((ImageCanvas)blobPanel.getChild()).setTool(i));

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
            blobPanel.getChild().repaint();
        });

        down.addActionListener((e) -> {
            int oldCount = blobs.size();
            do {
                if (distanceThreshold <= 2) break;
                distanceThreshold--;
                detectBlobs();
            } while (blobs.size() == oldCount);
            blobPanel.getChild().repaint();
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
        BlobCanvas blobCanvas = (BlobCanvas) blobPanel.getChild();

        blobCanvas.setBlobs(blobs);
        blobCanvas.setShowBlobs(showBlobs);
        blobCanvas.setShowPoints(showPoints);
    }

    private void updateCanvas() {
        detectBlobs();
        BlobCanvas blobCanvas = (BlobCanvas) blobPanel.getChild();
        blobCanvas.repaint();
    }
}