package com.poohcom1.spritesheetparser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteSheetParser {

    public static int[] findBackgroundColor(BufferedImage spriteSheet) {
        int[] colors = new int[1];
        colors[0] = spriteSheet.getRGB(0, 0);

        return colors;
    }
}
