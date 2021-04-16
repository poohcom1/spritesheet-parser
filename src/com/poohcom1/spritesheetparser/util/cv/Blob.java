package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Shapes2D.Point;
import com.poohcom1.spritesheetparser.util.Shapes2D.ShapesUtil;
import com.poohcom1.spritesheetparser.util.Shapes2D.Rect;

import java.util.ArrayList;
import java.util.List;

public class Blob extends Rect implements Comparable<Blob> {
    private final List<Point> points;

    // Init blob
    public Blob(int x, int y) {
        super(x, y, x, y);

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

        return ShapesUtil.squareDistance(x, y, clampX, clampY);
    }

    // If a blob is touching this blob
    public boolean shouldMerge(Blob other) {
        return intersects(other) || touches(other);
    }

    public boolean isNear(int x, int y, int threshold) {
        return squareDistance(x, y) < threshold;
    }

    // ORDERED BLOB FIELDS
    private int row = 0;
    private int column = 0;


    public void setRowColumn(int row, int column) {
        this.row = row; this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public int compareTo(Blob o) {
        return compareTo(o, BlobSequence.LEFT_TO_RIGHT, BlobSequence.TOP_TO_BOTTOM);
    }

    /*
        LEFT_TO_RIGHT = 0;
        TOP_TO_BOTTOM = 1;
        RIGHT_TO_LEFT = 2;
        BOTTOM_TO_TOP = 3;
     */
    public int compareTo(Blob other, int primaryOrder, int secondaryOrder) {
        if (primaryOrder == secondaryOrder) throw new IllegalArgumentException("Primary and secondary order cannot be the same.");
        if (primaryOrder > 3 || secondaryOrder > 3) throw new IllegalArgumentException("Illegal blob order constant.");

        final int[] origins = {this.x, this.y, other.x, other.y};

        // Primary/Secondary here refers to the axis that takes precedence in ordering
        int aPrimaryOrigin = origins[primaryOrder];
        int bPrimaryOrigin = origins[(primaryOrder + 2) % 4];
        int aSecondaryOrigin = origins[secondaryOrder];
        int bSecondaryOrigin = origins[(secondaryOrder + 2) % 4];

        if (overlapsOrder(other, primaryOrder)) {
            return aPrimaryOrigin - bPrimaryOrigin;
        } else {
            return aSecondaryOrigin - bSecondaryOrigin;
        }
    }

    // Check if object
    public boolean overlapsOrder(Blob other, int primaryOrder) {
        final int[] axes = {Rect.VERTICAL_AXIS, Rect.HORIZONTAL_AXIS};
        int subAxis = axes[(primaryOrder) % 2];

        return overlapsDirection(other, subAxis);
    }
}
