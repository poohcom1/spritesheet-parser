package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.PointHelper;
import com.poohcom1.spritesheetparser.util.Rect;

import java.util.ArrayList;

public class Blob {
    Point min, max;
    private ArrayList<Point> points;

    public Blob(int x, int y) {
        min = new Point(x, y);
        max = new Point(x, y);

        points = new ArrayList<>();
    }

    public Rect toRect() {
        return new Rect(min, max);
    }

    public ArrayList<Point> toPoints() {return points;}

    // Extends area to cover added pixels
    public void add(Point pos) {
        min.x = Math.min(min.x, pos.x);
        min.y = Math.min(min.y, pos.y);
        max.x = Math.max(max.x, pos.x);
        max.y = Math.max(max.y, pos.y);
        points.add(pos);
    }

    public void add(int x, int y) {
        min.x = Math.min(min.x, x);
        min.y = Math.min(min.y, y);
        max.x = Math.max(max.x, x);
        max.y = Math.max(max.y, y);
        points.add(new Point(x, y));
    }

    public int squareDistanceFromCenter(int x, int y) {
        return PointHelper.squareDistance(center(), new Point(x, y));
    }

    public int squareDistance(int x, int y) {
        int clampX = Math.min(max.x, Math.max(min.x, x));
        int clampY = Math.min(max.y, Math.max(min.y, y));

        return PointHelper.squareDistance(x, y, clampX, clampY);
    }

    public boolean isNear(int x, int y, int threshold) {
        return squareDistance(x, y) < threshold;
    }

    private Point center() {
        int cx = (min.x + max.x) / 2;
        int cy = (min.y + max.y) / 2;

        return new Point(cx, cy);
    }
}
