package com.poohcom1.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.kordamp.ikonli.swing.*;
import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.unicons.*;

public class App extends JFrame {
    // Icons
    public static Map<String, FontIcon> iconMap;

    // Components
    public JTabbedPane tabbedPane;

    // Tabs
    static final int SPRITE_EXTRACTION_PANE_TAB = 1;

    ImageTools imageTools;
    BlobDetectionTools blobDetectionTools;

    private static App app;

    public App() {
        super("Sprite Sheet Parser");

        prepareIcons();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        imageTools = new ImageTools(this);
        blobDetectionTools = new BlobDetectionTools();

        tabbedPane.addTab("Spritesheet Editing", imageTools.mainPanel);
        tabbedPane.addTab("Sprite Extraction", blobDetectionTools.mainPanel);

        tabbedPane.addChangeListener(l -> packInBounds());

        add(tabbedPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        try {
            BufferedImage icon = ImageIO.read(Objects.requireNonNull(App.class.getResource("/iconImage.png")));
            setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            app = new App();

            app.setVisible(true);
        });
    }

    /**
     * Courtesy of users/131872/camickr: https://stackoverflow.com/questions/40577930/java-set-maximum-size-of-jframe
     * Packs frame while making sure not to overlap with taskbar
     */
    void packInBounds() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        app.pack();
        app.revalidate();
        int width = Math.min(app.getWidth(), bounds.width);
        int height = Math.min(app.getHeight(), bounds.height);
        app.setSize(new Dimension(width, height));
    }

    public final static String ICON_MOVE = "Move";
    public final static String ICON_CROP = "Crop";
    public final static String ICON_COLOR = "Set transparent color";

    public final static String ICON_PLUS = "Plus";
    public final static String ICON_MINUS = "Minus";

    public final static String ICON_CUT = "Cut sprite box";
    public final static String ICON_DELETE = "Delete sprite box";
    public final static String ICON_MERGE = "Merge sprite box";

    public final static String ICON_EDIT_WARNING = "Warning: Re-detecting sprites will override your edits!";

    public final static String ICON_H_ALIGN_CENTER = "Align center horizontally";
    public final static String ICON_H_ALIGN_LEFT = "Align left";
    public final static String ICON_H_ALIGN_RIGHT = "Align right";
    public final static String ICON_V_ALIGN_CENTER = "Align center vertically";
    public final static String ICON_V_ALIGN_TOP = "Align top";
    public final static String ICON_V_ALIGN_BOTTOM = "Align bottom";

    public final static String ICON_PLAY = "Play";
    public final static String ICON_PAUSE = "Pause";

    static void prepareIcons() {
        iconMap = new HashMap<>();
        iconMap.put(ICON_H_ALIGN_CENTER, FontIcon.of(UniconsLine.HORIZONTAL_ALIGN_CENTER));
        iconMap.put(ICON_H_ALIGN_LEFT, FontIcon.of(UniconsLine.HORIZONTAL_ALIGN_LEFT));
        iconMap.put(ICON_H_ALIGN_RIGHT, FontIcon.of(UniconsLine.HORIZONTAL_ALIGN_RIGHT));
        iconMap.put(ICON_V_ALIGN_CENTER, FontIcon.of(UniconsLine.VERTICAL_ALIGN_CENTER));
        iconMap.put(ICON_V_ALIGN_TOP, FontIcon.of(UniconsLine.VERTICAL_ALIGN_TOP));
        iconMap.put(ICON_V_ALIGN_BOTTOM, FontIcon.of(UniconsLine.VERTICAL_ALIGN_BOTTOM));

        iconMap.put(ICON_PLUS, FontIcon.of(UniconsLine.PLUS));
        iconMap.put(ICON_MINUS, FontIcon.of(UniconsLine.MINUS));

        iconMap.put(ICON_EDIT_WARNING, FontIcon.of(BoxiconsSolid.ERROR));

        iconMap.put(ICON_MOVE, FontIcon.of(BoxiconsRegular.MOVE));
        iconMap.put(ICON_CROP, FontIcon.of(BoxiconsRegular.CROP));
        iconMap.put(ICON_COLOR, FontIcon.of(Material2RoundAL.COLORIZE));

        iconMap.put(ICON_CUT, FontIcon.of(BoxiconsRegular.CUT));
        iconMap.put(ICON_DELETE, FontIcon.of(BoxiconsSolid.ERASER));
        iconMap.put(ICON_MERGE, FontIcon.of(BoxiconsRegular.CUSTOMIZE));

        iconMap.values().forEach(icon -> icon.setIconSize(20));

        iconMap.put(ICON_PLAY, FontIcon.of(UniconsLine.PLAY));
        iconMap.put(ICON_PAUSE, FontIcon.of(UniconsLine.PAUSE));
    }

    // Courtesy of https://www.tutorialspoint.com/swingexamples/show_file_chooser_images_only.htm
    public final static String JPEG = "jpeg";
    public final static String JPG = "jpg";
    public final static String GIF = "gif";
    public final static String TIFF = "tiff";
    public final static String TIF = "tif";
    public final static String PNG = "png";

    static FileNameExtensionFilter getImageExtensions(String extensionConstant) {
        return new FileNameExtensionFilter(extensionConstant.toUpperCase(), extensionConstant);
    }

    static class ImageFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if (extension != null) {
                return extension.equals(TIFF) ||
                        extension.equals(TIF) ||
                        extension.equals(GIF) ||
                        extension.equals(JPEG) ||
                        extension.equals(JPG) ||
                        extension.equals(PNG);
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

}