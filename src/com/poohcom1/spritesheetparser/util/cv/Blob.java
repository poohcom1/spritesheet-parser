package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.PointUtil;
import com.poohcom1.spritesheetparser.util.Rect;

import java.util.ArrayList;

public class Blob implements Comparable {
    private Point min, max;

    private ArrayList<Point> points;

    private int ordering;

    // Init blob
    public Blob(int x, int y) {
        min = new Point(x, y);
        max = new Point(x+1, y+1);

        points = new ArrayList<>();
        ordering = BlobDetector.LEFT_TO_RIGHT;
    }

    public Blob(int x, int y, int ordering) {
        min = new Point(x, y);
        max = new Point(x+1, y+1);

        points = new ArrayList<>();
        this.ordering = ordering;
    }

    // New blob from merging blobs blobs
    public Blob(Blob blob1, Blob blob2) {
        int minX = Math.min(blob1.min.x, blob2.min.x);
        int minY = Math.min(blob1.min.y, blob2.min.y);
        int maxX = Math.max(blob1.max.x, blob2.max.x);
        int maxY = Math.max(blob1.max.y, blob2.max.y);

        min = new Point(minX, minY);
        max = new Point(maxX, maxY);

        points = blob1.points;
        points.addAll(blob2.points);

        // Sprite direction must be the same in two blobs from the same detection
        ordering = blob1.ordering;
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

    public int squareDistance(int x, int y) {
        int clampX = Math.min(max.x, Math.max(min.x, x));
        int clampY = Math.min(max.y, Math.max(min.y, y));

        return PointUtil.squareDistance(x, y, clampX, clampY);
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

    @Override
    public int compareTo(Object o) {
        int mainAxis;
        int subAxis;
        boolean forwardDirection;

        switch (ordering) {
            case BlobDetector.LEFT_TO_RIGHT -> {
                mainAxis = min.x;
                subAxis = min.y;
                forwardDirection = true;
            }
            case BlobDetector.TOP_TO_BOTTOM -> {
                mainAxis = min.y;
                subAxis = min.x;
                forwardDirection = true;
            }
            case BlobDetector.RIGHT_TO_LEFT -> {
                mainAxis = min.x;
                subAxis = min.y;
                forwardDirection = false;
            }
            case BlobDetector.BOTTOM_TO_TOP -> {
                mainAxis = min.y;
                subAxis = min.x;
                forwardDirection = false;
            }
        }

        // TODO: Do this

        return 0;
    }
}
