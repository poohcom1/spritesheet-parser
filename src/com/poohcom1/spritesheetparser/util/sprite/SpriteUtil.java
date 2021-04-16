package com.poohcom1.spritesheetparser.util.sprite;

import com.poohcom1.spritesheetparser.util.Shapes2D.Rect;
import com.poohcom1.spritesheetparser.util.cv.Blob;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.List;

public class SpriteUtil {
    public static List<Sprite> extractBlobSprites(BufferedImage image, List<Blob> blobs) {
        List<Sprite> sprites = new ArrayList<>();

        for (Rect crop : blobs) {
            BufferedImage subImage;

            subImage = image.getSubimage(crop.x, crop.y, crop.width + 1, crop.height + 1);

            sprites.add(new Sprite(subImage));
        }

        return sprites;
    }


    public static Dimension spriteMaxDimension(Sprite[] sprites) {
        int maxHeight = 0;
        int maxWidth = 0;

        for (Sprite sprite: sprites) {
            if (sprite.getOriginalWidth() > maxWidth) {
                maxWidth = sprite.getOriginalWidth();
            }

            if (sprite.getOriginalHeight() > maxHeight) {
                maxHeight = sprite.getOriginalHeight();
            }
        }

        return new Dimension(maxWidth, maxHeight);
    }

    /**
     * Calculates the FPS given the milliseconds per frame.
     * @param msPerFrame Milliseconds per frame
     * @return Frames per second
     */
    public static float fpsFromMs(float msPerFrame) {
        return ((1 / msPerFrame) * 1000);
    }

    public static float MsFromFps(float fps)  {
        return ((1/fps) * 1000);
    }
}
