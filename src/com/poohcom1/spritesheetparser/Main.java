package com.poohcom1.spritesheetparser;

import com.poohcom1.spritesheetparser.util.cv.BlobDetector;
import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.window.MyCanvas;
import com.poohcom1.spritesheetparser.window.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    private int[] alphaColor;

    public static void main(String[] args) throws IOException {
        BufferedImage spriteSheet = loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png");

        int[] backgroundColor = SpriteSheetParser.findBackgroundColor(spriteSheet);



        new Window(spriteSheet, backgroundColor);
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }
}
