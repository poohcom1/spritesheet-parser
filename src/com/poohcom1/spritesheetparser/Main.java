package com.poohcom1.spritesheetparser;

import com.poohcom1.spritesheetparser.util.PointHelper;
import com.poohcom1.spritesheetparser.util.cv.BlobDetector;
import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.window.MyCanvas;
import com.poohcom1.spritesheetparser.window.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(PointHelper.squareDistance(0, 0, 1, 1));

        BufferedImage spriteSheet = loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png");

        System.out.println("W: " + spriteSheet.getWidth() + ", H: " + spriteSheet.getHeight());

        int[] backgroundColor = SpriteSheetParser.findBackgroundColor(spriteSheet);

        Window window = new Window(spriteSheet, backgroundColor);
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }
}
