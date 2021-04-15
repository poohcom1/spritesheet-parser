package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.PointUtil;
import com.poohcom1.spritesheetparser.util.Rect;

import java.util.ArrayList;
import java.util.List;

public class Blob extends Rect {
    private final List<Point> points;

    // Init blob
    public Blob(int x, int y) {
        super(x, y, x + 1, y + 1);

        points = new ArrayList<>();
    }

    // New blob from merging blobs blobs
    public Blob(Blob blob1, Blob blob2) {
        super(Math.min(blob1.x, blob2.x), Math.min(blob1.y, blob2.y),
                Math.max(blob1.x + blob1.width, blob2.x + blob2.width), Math.max(blob1.y + blob1.height, blob2.y + blob2.height));

        points = blob1.points;
        points.addAll(blob2.points);

        // Sprite direction must be the same in two blobs from the same detection
    }

    public Blob(int minX, int minY, int maxX, int maxY) {
        super(minX, minY, maxX, maxY);

        this.points = new ArrayList<>();
    }

    public List<Point> getPoints() {return points;}

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

    public int compareTo(Object o) {
        return compareTo(o, BlobDetector.LEFT_TO_RIGHT, BlobDetector.TOP_TO_BOTTOM);
    }

    /*
        LEFT_TO_RIGHT = 0;
        TOP_TO_BOTTOM = 1;
        RIGHT_TO_LEFT = 2;
        BOTTOM_TO_TOP = 3;
     */
    public int compareTo(Object o, int primaryOrder, int secondaryOrder) {
        if (primaryOrder == secondaryOrder)
            throw new IllegalArgumentException("Primary and secondary order cannot be the same.");
        if (primaryOrder > 3 || secondaryOrder > 3)
            throw new IllegalArgumentException("Illegal blob order constant.");

        Blob other = (Blob) o;
        final int[] origins = {this.x, this.y, other.x, other.y};
        final int[] axes = {Rect.HORIZONTAL_AXIS, Rect.VERTICAL_AXIS};

        // Primary/Secondary here refers to the axis that takes precedence in ordering
        int aPrimaryOrigin = origins[primaryOrder];
        int bPrimaryOrigin = origins[(primaryOrder + 2) % 4];
        int aSecondaryOrigin = origins[secondaryOrder];
        int bSecondaryOrigin = origins[(secondaryOrder + 2) % 4];

        // Axis to check
        int subAxis = axes[secondaryOrder % 2];

        if (overlapsDirection(other, subAxis)) {
            return aPrimaryOrigin - bPrimaryOrigin;
        } else {
            return aSecondaryOrigin - bSecondaryOrigin;
        }
    }

}
