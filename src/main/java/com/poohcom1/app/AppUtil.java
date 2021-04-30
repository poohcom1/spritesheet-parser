package com.poohcom1.app;

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

    public static File saveImage(BufferedImage image, String path, String name, String formatName) throws  IOException {
        File output = new File(path + "/" + name);
        ImageIO.write(image, formatName, output);
        return output;
    }
}
