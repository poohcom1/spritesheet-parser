package com.poohcom1.spritesheetparser.cv;

import com.poohcom1.spritesheetparser.shapes2D.Rect;
import com.poohcom1.spritesheetparser.shapes2D.ShapesUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Blob extends Rect implements Comparable<Blob> {
    private final List<Point> points;

    // Init blob
    public Blob(int x, int y) {
        super(x, y, x, y);

        points = new ArrayList<>();
        points.add(new Point(x, y));
    }

    public Blob(int minX, int minY, int maxX, int maxY) {
        super(minX, minY, maxX, maxY);

        this.points = new ArrayList<>();
    }

    // New blob from merging blobs blobs
    public Blob(Blob blob1, Blob blob2) {
        super(Math.min(blob1.x, blob2.x), Math.min(blob1.y, blob2.y),
                Math.max(blob1.x + blob1.width, blob2.x + blob2.width), Math.max(blob1.y + blob1.height, blob2.y + blob2.height));

        points = blob1.points;
        points.addAll(blob2.points);
    }

    public List<Point> getPoints() {return points;}

    public boolean removePoint(Point point) {
        points.remove(point);
        setDimensionsFromPoints();
        return points.size() == 0;
    }

    private void setDimensionsFromPoints() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (Point p: points) {
            if (p.x < minX) minX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }

        x = minX;
        y = minY;
        width = maxX - x;
        height = maxY - y;
    }

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

    /**
     * Detects if current blob is within/contained by or touching the given blob
     * @param other The blob to check against
     * @return Whether or not the blobs should be merged
     */
    public boolean shouldMerge(Blob other) {
        return intersects(other) || touches(other) || contains(other) || other.contains(this);
    }

    public boolean isNear(int x, int y, int threshold) {
        return squareDistance(x, y) < threshold;
    }

    // ====================== ORDERED BLOB FIELDS =================================

    private int row = -1;
    private int column = -1;


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

        if (overlapsByOrder(other, primaryOrder)) {
            return aPrimaryOrigin - bPrimaryOrigin;
        } else {
            return aSecondaryOrigin - bSecondaryOrigin;
        }
    }

    // Check if object
    public boolean overlapsByOrder(Blob other, int primaryOrder) {
        final int[] axes = {Rect.VERTICAL_AXIS, Rect.HORIZONTAL_AXIS};
        int subAxis = axes[(primaryOrder) % 2];

        return overlapsDirection(other, subAxis);
    }
}
