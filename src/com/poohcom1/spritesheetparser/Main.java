package com.poohcom1.spritesheetparser;

import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.cv.BlobDetector;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;
import com.poohcom1.spritesheetparser.window.BlobWindow;
import com.poohcom1.spritesheetparser.window.SpriteWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage loadedImage = loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet2.png");
        BufferedImage spriteSheet = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        spriteSheet.getGraphics().drawImage(loadedImage, 0, 0, null);

        // Replace background color with alpha
        int alpha = new Color(0, 0, 0, 0).getRGB();
        int[] background = ImageUtil.findBackgroundColor(spriteSheet);
        spriteSheet = ImageUtil.replaceColors(spriteSheet, background, alpha);

        // Start window


        ArrayList<Blob> blobs = BlobDetector.detectBlobs(spriteSheet, new int[] {alpha}, 18);


        Sprite[] sprites = SpriteUtil.extractSpritesBlobs(spriteSheet, blobs);

        new BlobWindow(spriteSheet, new int[] {alpha});
        new SpriteWindow(sprites, 12);
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    private static File saveImage(BufferedImage image, String name, String formatName) throws  IOException {
        File output = new File("src/com/poohcom1/spritesheetparser/assets/sprites/" + name + "." + formatName);
        ImageIO.write(image, formatName, output);
        return output;
    }
}
