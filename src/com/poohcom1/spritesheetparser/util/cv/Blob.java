package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.PointHelper;
import com.poohcom1.spritesheetparser.util.Rect;

class Blob {
    Point min, max;

    public Blob(int x, int y) {
        min = new Point(x, y);
        max = new Point(x, y);
    }

    public Rect toRect() {
        return new Rect(min, max);
    }

    // Extends area to cover added pixels
    public void add(Point pos) {
        min.x = Math.min(min.x, pos.x);
        min.y = Math.min(min.y, pos.y);
        max.x = Math.max(max.x, pos.x);
        max.y = Math.max(max.y, pos.y);
    }

    public void add(int x, int y) {
        min.x = Math.min(min.x, x);
        min.y = Math.min(min.y, y);
        max.x = Math.max(max.x, x);
        max.y = Math.max(max.y, y);
    }

    public int squareDistance(int x, int y) {
        return PointHelper.squareDistance(center(), new Point(x, y));
    }

    public boolean isNear(Point pos, int threshold) {
        Point thisCenter = center();

        int sqrDist = PointHelper.squareDistance(thisCenter, pos);

        return sqrDist < threshold;
    }

    public boolean isNear(int x, int y, int threshold) {
        Point thisCenter = center();

        int sqrDist = PointHelper.squareDistance(thisCenter.x, thisCenter.y, x, y);

        return sqrDist < threshold;
    }

    private Point center() {
        int cx = (min.x + max.x) / 2;
        int cy = (min.y + max.y) / 2;

        return new Point(cx, cy);
    }
}
