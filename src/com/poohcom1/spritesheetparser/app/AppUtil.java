package com.poohcom1.spritesheetparser.app;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AppUtil {
    public static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    public static BufferedImage loadImage(File f) throws IOException {
        return ImageIO.read(f);
    }

    public static File saveImage(BufferedImage image, String name, String formatName) throws  IOException {
        File output = new File("src/com/poohcom1/spritesheetparser/assets/sprites/" + name + "." + formatName);
        ImageIO.write(image, formatName, output);
        return output;
    }
}
