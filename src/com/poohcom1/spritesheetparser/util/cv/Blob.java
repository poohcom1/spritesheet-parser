package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.PointHelper;
import com.poohcom1.spritesheetparser.util.Rect;

import java.util.ArrayList;

public class Blob {
    private Point min, max;

    private ArrayList<Point> points;

    public Blob(int x, int y) {
        min = new Point(x, y);
        max = new Point(x+1, y+1);

        points = new ArrayList<>();
    }

    public Blob(int minX, int minY, int maxX, int maxY, ArrayList<Point> points) {
        min = new Point(minX, minY);
        max = new Point(maxX, maxY);

        this.points = points;
    }

    public Rect toRect() {
        return new Rect(min, max);
    }

    public ArrayList<Point> toPoints() {return points;}

    // Extends area to cover added pixels
    public void add(int x, int y) {
        min.x = Math.min(min.x, x);
        min.y = Math.min(min.y, y);
        max.x = Math.max(max.x, x);
        max.y = Math.max(max.y, y);
        points.add(new Point(x, y));
    }

    // Returns a new combined blob
    public Blob merge(Blob blob) {
        int minX = Math.min(min.x, blob.min.x);
        int minY = Math.min(min.y, blob.min.y);
        int maxX = Math.max(max.x, blob.max.x);
        int maxY = Math.max(max.y, blob.max.y);

        ArrayList<Point> combinedPoints = points;
        combinedPoints.addAll(blob.points);

        return new Blob(minX, minY, maxX, maxY, combinedPoints);
    }

    public int squareDistance(int x, int y) {
        int clampX = Math.min(max.x, Math.max(min.x, x));
        int clampY = Math.min(max.y, Math.max(min.y, y));

        return PointHelper.squareDistance(x, y, clampX, clampY);
    }

    // If a blob is touching this blob
    public boolean isTouching(Blob other) {
        return toRect().intersects(other.toRect()) || toRect().touches(other.toRect());
    }

    public boolean isNear(int x, int y, int threshold) {
        return squareDistance(x, y) < threshold;
    }

    public String toString() {
        return toRect().toString();
    }
}
