package com.poohcom1.app;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AppUtil {
    public static BufferedImage loadImage(File f) throws IOException {
        BufferedImage image = ImageIO.read(f);

        if (image.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            convertedImg.getGraphics().drawImage(image, 0, 0, null);
            return convertedImg;
        }

        return image;
    }

    public static File saveImage(BufferedImage image, String path, String name, String formatName) throws  IOException {
        File output = new File(path + "/" + name);
        ImageIO.write(image, formatName, output);
        return output;
    }
}
