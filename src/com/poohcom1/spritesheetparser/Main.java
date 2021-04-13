package com.poohcom1.spritesheetparser;

import com.poohcom1.spritesheetparser.util.image.ImageHelper;
import com.poohcom1.spritesheetparser.window.BlobWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage spriteSheet = loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png");

        System.out.println("W: " + spriteSheet.getWidth() + ", H: " + spriteSheet.getHeight());

        int alpha = new Color(0, 0, 0, 0).getRGB();

        int[] background = ImageHelper.findBackgroundColor(spriteSheet);

        spriteSheet = ImageHelper.replaceColors(spriteSheet, background, alpha);

//        int[] backgroundColor = SpriteSheetParser.findBackgroundColor(spriteSheet);

//        int[] blobCount = new int[15];
//        for (int i = 0; i < blobCount.length; i++) {
//            blobCount[i] = BlobDetector.detectBlobs(spriteSheet, backgroundColor, i).size();
//            System.out.println(blobCount[i]);
//        }

        BlobWindow window = new BlobWindow(spriteSheet, new int[] {alpha});
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }
}
