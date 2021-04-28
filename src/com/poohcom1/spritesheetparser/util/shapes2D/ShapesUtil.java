package com.poohcom1.spritesheetparser.util.shapes2D;

import com.poohcom1.spritesheetparser.util.cv.Blob;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class ShapesUtil {
    public ShapesUtil() {
    }

    public static int squareDistance(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public static int squareDistance(Point p1, Point p2) {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
    }

    public static Rect maxBoundaries(List<? extends Rectangle> sprites) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (Rectangle sprite : sprites) {
            if (sprite.x < minX) minX = sprite.x;
            if (((Rect)sprite).maxX() > maxX) maxX = ((Rect)sprite).maxX();
            if (sprite.y < minY) minY = sprite.y;
            if (((Rect)sprite).maxY() > maxY) maxY = ((Rect)sprite).maxY();
        }
        return new Rect(minX, minY, maxX, maxY);
    }

    /**
     * Calculates the maximum dimensions of the rectangles in the list
     * @param rectangles List of rectangles
     * @return A Dimension object of the maximum dimensions
     */
    public static Dimension maxDimensions(List<? extends Rectangle> rectangles) {
        int maxHeight = 0;
        int maxWidth = 0;

        for (Rectangle sprite : rectangles) {
            if (sprite.getWidth() > maxWidth) {
                maxWidth = (int) sprite.getWidth();
            }

            if (sprite.getHeight() > maxHeight) {
                maxHeight = (int) sprite.getHeight();
            }
        }
        return new Dimension(maxWidth, maxHeight);
    }


    public static int[] findBaselines(List<? extends Rect> rectangles) {
        int minSideCount = Integer.MAX_VALUE;
        int[] minSidePositions = new int[0];

        for (int side = 0; side < 4; side++) {
            HashSet<Integer> sidePositions = new HashSet<>();
            for (Rect rectangle : rectangles) {
                sidePositions.add(rectangle.getSides()[side]);
            }

            if (sidePositions.size() < minSideCount) {
                minSidePositions = sidePositions.stream().mapToInt(i -> i).toArray();
                minSideCount = sidePositions.size();
            }
        }

        return minSidePositions;
    }

    public static Rect mergeRects(List<? extends Rect> rects) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (Rect rect: rects) {
            if (rect.x < minX) minX = rect.x;
            if (rect.y < minY) minY = rect.y;
            if (rect.maxX() > maxX) maxX = rect.maxX();
            if (rect.maxY() > maxY) maxY = rect.maxY();
        }

        return new Rect(minX, minY, maxX, maxY);
    }
}
