package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.PointUtil;
import com.poohcom1.spritesheetparser.util.Rect;

import java.util.ArrayList;

public class Blob extends Rect implements Comparable {
    private final ArrayList<Point> points;
    private int ordering;

    // Init blob
    public Blob(int x, int y) {
        super(x, y, x + 1, y + 1);

        points = new ArrayList<>();
        ordering = BlobDetector.LEFT_TO_RIGHT;
    }

    public Blob(int x, int y, int ordering) {
        super(x, y, x + 1, y + 1);

        points = new ArrayList<>();
        this.ordering = ordering;
    }

    // New blob from merging blobs blobs
    public Blob(Blob blob1, Blob blob2) {
        super(Math.min(blob1.x, blob2.x), Math.min(blob1.y, blob2.y),
                Math.max(blob1.x + blob1.width, blob2.x + blob2.width), Math.max(blob1.y + blob1.height, blob2.y + blob2.height));

        points = blob1.points;
        points.addAll(blob2.points);

        // Sprite direction must be the same in two blobs from the same detection
        ordering = blob1.ordering;
    }

    public Blob(int minX, int minY, int maxX, int maxY, ArrayList<Point> points) {
        super(minX, minY, maxX, maxY);

        this.points = points;
    }


    public ArrayList<Point> toPoints() {return points;}

    // Extends area to cover added pixels
    public void add(int x, int y) {
        super.add(x, y);

        points.add(new Point(x, y));
    }

    public int squareDistance(int x, int y) {
        int clampX = Math.min(this.x + width, Math.max(this.x, x));
        int clampY = Math.min(this.y + height,  Math.max(this.y, y));

        return PointUtil.squareDistance(x, y, clampX, clampY);
    }

    // If a blob is touching this blob
    public boolean shouldMerge(Blob other) {
        return intersects(other) || touches(other);
    }

    public boolean isNear(int x, int y, int threshold) {
        return squareDistance(x, y) < threshold;
    }

    @Override
    public int compareTo(Object o) {
        switch (ordering) {
            case BlobDetector.LEFT_TO_RIGHT -> {

            }
            case BlobDetector.TOP_TO_BOTTOM -> {

            }
            case BlobDetector.RIGHT_TO_LEFT -> {

            }
            case BlobDetector.BOTTOM_TO_TOP -> {

            }
        }




        return 0;
    }
}
