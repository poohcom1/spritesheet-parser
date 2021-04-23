package com.poohcom1.spritesheetparser.util.image;

import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;
import com.poohcom1.spritesheetparser.util.sprite.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUtil {
    public static String colorIntToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static String colorIntToHexAlpha(int color) {
        return String.format("#%08X", (0xFFFFFF & color));
    }

    public static int[] rbgaIntToArray(int rgba) {
        int b = rgba & 0x00ff;
        int g = (rgba >> 8) & 0x00ff;
        int r = (rgba >> 16) & 0x00ff;
        int a = (rgba >> 24) & 0x00ff;
        return new int[] {r, g, b, a};
    }

    public static int rgbaArrayToInt(int[] rgba) {
        return (rgba[3] << 24) + (rgba[0] << 16) + (rgba[1] << 8) + rgba[2];
    }

    public static int[] findBackgroundColor(BufferedImage spriteSheet) {
        int[] colors = new int[1];
        colors[0] = spriteSheet.getRGB(0, 0);

        return colors;
    }

    public interface PixelEditor {
        int[] editPixel(int[] rgba, int x, int y);
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

    public static BufferedImage replaceColors(BufferedImage image, int[] colors, int replacementColor) {
        return pointProcessing(image, (rgba, x, y) -> {
            for (int color : colors) {
                int pixel = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).getRGB();

                if (pixel == color) {
                    return rbgaIntToArray(replacementColor);
                }
            }
            return rgba;
        });
    }

    public static BufferedImage alignImage(BufferedImage image, int xOffset, int yOffset) {
        BufferedImage alignedImage = new BufferedImage(image.getWidth() + xOffset, image.getHeight() + yOffset, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics g = alignedImage.getGraphics();

        if (xOffset < 0) {
            image = image.getSubimage(-xOffset, 0, image.getWidth() + xOffset, image.getHeight());

            xOffset = 0;
        }

        if (yOffset < 0) {
            image = image.getSubimage(0, -yOffset, image.getWidth(), image.getHeight() + yOffset);

            yOffset = 0;
        }

        // First value of background image should be the on to be expanded
        int bgC = ImageUtil.findBackgroundColor(image)[0];

        int[] bg = rbgaIntToArray(bgC);

        g.setColor(new Color(bg[0], bg[1], bg[2], bg[3]));

        g.fillRect(0, 0, alignedImage.getWidth(), alignedImage.getHeight());

        g.drawImage(image, xOffset, yOffset, null);

        return alignedImage;
    }

    public static BufferedImage deepCopyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}

