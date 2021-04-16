package com.poohcom1.spritesheetparser.util.Shapes2D;

import java.awt.*;
import java.util.*;
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
}
