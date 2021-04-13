package com.poohcom1.spritesheetparser.util.image;

import com.poohcom1.spritesheetparser.util.Rect;
import com.poohcom1.spritesheetparser.util.cv.Blob;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class ImageHelper {
    public static String colorIntToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static String colorIntToHexAlpha(int color) {
        return String.format("#%08X", (0xFFFFFF & color));
    }

    public static int[] findBackgroundColor(BufferedImage spriteSheet) {
        int[] colors = new int[1];
        colors[0] = spriteSheet.getRGB(0, 0);

        return colors;
    }

    public interface PixelEditor {
        public int[] editPixel(int[] rgba, int x, int y);
    }

    public static BufferedImage pointProcessing(BufferedImage image, PixelEditor pixelEditor) {
        return pointProcessing(image, pixelEditor, true);
    }

    public static BufferedImage pointProcessing(BufferedImage image, PixelEditor pixelEditor, boolean loopVertical) {
        ColorModel colorModel = image.getColorModel();

        WritableRaster imageRaster = image.getRaster();
        WritableRaster newRaster = colorModel.createCompatibleWritableRaster(image.getWidth(), image.getHeight());

        int iMax = image.getHeight();
        int jMax= image.getWidth();

        if (!loopVertical) {
            iMax = image.getWidth(); jMax = image.getHeight();
        }

        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++) {
                int x = j; int y = i;
                if (!loopVertical) {x = i; y = j;}

                int[] pixel = imageRaster.getPixel(x, y, (int[]) null);

                int[] processedPixel = pixelEditor.editPixel(pixel, x, y);

                newRaster.setPixel(x, y, processedPixel);
            }
        }

        return new BufferedImage(colorModel, newRaster, colorModel.isAlphaPremultiplied(), null);
    }

    public static int[] getRGBAArray(int rgba) {
        int g = rgba & 0x00ff;
        int b = (rgba >> 8) & 0x00ff;
        int r = (rgba >> 16) & 0x00ff;
        int a = (rgba >> 24) & 0x00ff;
        return new int[] {r, g, b, a};
    }

    public static int getRGBAValue(int[] rgba) {
        return (rgba[3] << 24) + (rgba[0] << 16) + (rgba[1] << 8) + rgba[2];
    }

    public static BufferedImage replaceColors(BufferedImage image, int[] colors, int replacementColor) {
        return pointProcessing(image, (rgba, x, y) -> {
            for (int color : colors) {
                int pixel = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).getRGB();

                if (pixel == color) {
                    return getRGBAArray(replacementColor);
                }
            }
            return rgba;
        });
    }

    public static BufferedImage[] extractSpritesBlobs(BufferedImage image, ArrayList<Blob> blobs) {
        BufferedImage[] sprites = new BufferedImage[blobs.size()];

        for (int i = 0; i < blobs.size(); i++) {
            Rect crop = blobs.get(i).toRect();

            sprites[i] = image.getSubimage(crop.x, crop.y, crop.width, crop.height);
        }
        return sprites;
    }
}

