package com.poohcom1.spritesheetparser.util.image;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.*;
import java.util.List;

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

    public static Color rgbaIntToColor(int rgba) {
        return new Color(rgba & 0x00ff, (rgba >> 8) & 0x00ff, (rgba >> 16) & 0x00ff, (rgba >> 24) & 0x00ff);
    }

    /**
     * Attempts to find the background color of an image by checking the corner and side pixels and finding the most
     * common color.
     * @param spriteSheet Sprite-sheet to find the background color of
     * @return A array of size 1 of the background color
     */
    public static int[] findBackgroundColor(BufferedImage spriteSheet) {
        List<Integer> colorCounter = new ArrayList<>();

        final int STEPS = 10;

        for (int i = 0; i < STEPS-1; i++) {
            int x = (spriteSheet.getWidth()/STEPS) * i;
            colorCounter.add(spriteSheet.getRGB(x, 0));
            colorCounter.add(spriteSheet.getRGB(x, spriteSheet.getHeight()-1));
        }

        for (int i = 0; i < STEPS-1; i++) {
            int y = (spriteSheet.getHeight()/STEPS) * i;
            colorCounter.add(spriteSheet.getRGB(0, y));
            colorCounter.add(spriteSheet.getRGB(spriteSheet.getWidth()-1, y));
        }

        return new int[] {ImageUtil.getMode(colorCounter).get(0)};
    }

    public static <T> List<T> getMode(List<T> list) {
        List <T> mostCommonItems = new ArrayList<>();
        int maxCount = 0;

        // Find most common edge pixel
        Map<T, Integer> itemMap = new HashMap<>();

        for (T item: list) {
            if (itemMap.containsKey(item)) {
                itemMap.put(item, itemMap.get(item) + 1);
            } else {
                itemMap.put(item, 1);
            }

            if (itemMap.get(item) > maxCount) {
                mostCommonItems.clear();
                mostCommonItems.add(item);
                maxCount = itemMap.get(item);
            } else if (itemMap.get(item) == maxCount) {
                mostCommonItems.add(item);
            }
        };

        return mostCommonItems;
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

    public static BufferedImage pointProcessingBuffered(BufferedImage image, PixelEditor pixelEditor) {
        int iMax = image.getHeight();
        int jMax= image.getWidth();

        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++) {

                int pixel = image.getRGB(j, i);

                int[] processedPixel = pixelEditor.editPixel(rbgaIntToArray(pixel), j, i);

                image.setRGB(j, i, rgbaArrayToInt(processedPixel));
            }
        }

        return image;
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

    public static BufferedImage replaceColorsBuffered(BufferedImage image, int[] colors, int replacementColor) {
        return pointProcessingBuffered(deepCopyImage(image), (rgba, x, y) -> {
            for (int color : colors) {
                int pixel = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).getRGB();

                if (pixel == color) {
                    return rbgaIntToArray(replacementColor);
                }
            }
            return rgba;
        });
    }

    public static BufferedImage alignImage(BufferedImage image, int xOffset, int yOffset, int backgroundColor) {
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

        g.setColor(rgbaIntToColor(backgroundColor));

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

    public BufferedImage createColoredRectangle(int width, int height, Color color) {
        BufferedImage square = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = square.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        return square;
    }
}

