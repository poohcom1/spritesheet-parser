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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage spriteSheet = loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png");

        // Replace background color with alpha
        int alpha = new Color(0, 0, 0, 0).getRGB();
        int[] background = ImageUtil.findBackgroundColor(spriteSheet);
        spriteSheet = ImageUtil.replaceColors(spriteSheet, background, alpha);

        // Start window
        new BlobWindow(spriteSheet, new int[] {alpha});

        ArrayList<Blob> blobs = BlobDetector.detectBlobs(spriteSheet, new int[] {alpha}, 2);
        BlobDetector.mergeBlobs(blobs);

        Sprite[] sprites = SpriteUtil.extractSpritesBlobs(spriteSheet, blobs);

        for (int i = 0; i < sprites.length; i++) {
            saveImage(sprites[i].getSprite(), "" + i, "png");
        }

        //new SpriteWindow(sprites, 12);
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
