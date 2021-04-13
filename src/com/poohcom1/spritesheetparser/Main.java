package com.poohcom1.spritesheetparser;

import com.poohcom1.spritesheetparser.window.BlobWindow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage spriteSheet = loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png");

        System.out.println("W: " + spriteSheet.getWidth() + ", H: " + spriteSheet.getHeight());

        int[] backgroundColor = SpriteSheetParser.findBackgroundColor(spriteSheet);

//        int[] blobCount = new int[15];
//        for (int i = 0; i < blobCount.length; i++) {
//            blobCount[i] = BlobDetector.detectBlobs(spriteSheet, backgroundColor, i).size();
//            System.out.println(blobCount[i]);
//        }

        BlobWindow window = new BlobWindow(spriteSheet, backgroundColor);
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }
}
