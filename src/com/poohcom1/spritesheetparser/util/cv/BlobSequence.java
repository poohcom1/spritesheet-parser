package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;
import com.poohcom1.spritesheetparser.util.shapes2D.ShapesUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class BlobSequence extends ArrayList<Blob> {
    public static final int LEFT_TO_RIGHT = 0;
    public static final int TOP_TO_BOTTOM = 1;
    public static final int RIGHT_TO_LEFT = 2;
    public static final int BOTTOM_TO_TOP = 3;

    private final int primaryOrder;
    private final int secondaryOrder;

    public BlobSequence(BufferedImage image, int[] backgroundColor, int threshold, int primaryOrder, int secondaryOrder) {
        super(detectBlobs(image, backgroundColor, threshold));

        sort((a, b) -> a.compareTo(b, primaryOrder, secondaryOrder));

        this.primaryOrder = primaryOrder;
        this.secondaryOrder = secondaryOrder;
    }

    public BlobSequence(List<Blob> unorderedBlobs, int primaryOrder, int secondaryOrder) {
        super(unorderedBlobs);

        sort((a, b) -> a.compareTo(b, primaryOrder, secondaryOrder));

        this.primaryOrder = primaryOrder;
        this.secondaryOrder = secondaryOrder;
    }

    public String toString() {
        StringBuilder text = new StringBuilder();

        int row = 0;
        for (int i = 0; i < size();) {
            if (get(i).getRow() == row) {
                text.append(get(i).getRow())
                        .append(",")
                        .append(get(i).getColumn())
                        .append(": ")
                        .append(i)
                        .append("\t\t");
                i++;
            } else {
                row++;
                text.append("\n");
            }
        }

        return text.toString();
    }


    public Point[] toPoints() {
        return blobsToPoints(this);
    }

    @Override
    public void sort(Comparator<? super Blob> c) {
        super.sort(c);

        int row = 0;
        int column = 0;

        get(0).setRowColumn(0, 0);

        for (int i = 1; i < size(); i++) {
            Blob current = get(i);
            Blob previous = get(i-1);


            if (current.overlapsByOrder(previous, primaryOrder)) {
                column++;
            } else {
                column = 0;
                row += 1;
            }

            current.setRowColumn(row, column);
            set(i, current);
        }
    }


    public int rowOf(Blob o) {
        return rowOf(indexOf(o));
    }
    public int columnOf(Blob o) {
        return columnOf(indexOf(o));
    }

    public int rowOf(int index) {
        return get(index).getRow();
    }

    public int columnOf(int index) {
        return get(index).getColumn();
    }

    public int rows() {
        return rowOf(size() - 1) + 1;
    }

    public List<Blob> getRow(int row) {
        if (row < 0 || row >= rows()) {
            return new ArrayList<Blob>();
        }

        int start = 0;
        while (start < size() && rowOf(start) != row) start++;
        int end = start;
        while (end < size() && rowOf(end) == row) end++;

        return subList(start, end);
    }

    public List<List<Blob>> getRows() {
        List<List<Blob>> rows = new ArrayList<>();

        for (int row = 0; row < rows(); row++) {
            rows.add(getRow(row));
        }

        return rows;
    }

    public Dimension getDimensions() {
        int width = 0;
        int height = 0;

        for (List<Blob> row: getRows()) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = 0;
            int maxY = 0;

            Rect currentBoundaries = ShapesUtil.maxBoundaries(row);
            minX = Math.min(minX, currentBoundaries.x);
            minY = Math.min(minY, currentBoundaries.y);
            maxX = Math.max(maxX, currentBoundaries.maxX());
            maxY = Math.max(maxY, currentBoundaries.maxY());

            width = Math.max(width, maxX - minX);
            height = Math.max(height, maxY - minY);
        }

        return new Dimension(width, height);
    }

    public int getPrimaryOrder() {
        return primaryOrder;
    }

    public boolean ordersHorizontally() {return primaryOrder == LEFT_TO_RIGHT || primaryOrder == RIGHT_TO_LEFT;}

    public static List<Blob> detectDiscreteBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold) {
        List<Blob> blobList = new ArrayList<>();

        ImageUtil.pointProcessing(image, (rgba, x, y) -> {
            boolean skip = false;

            int pixel = ImageUtil.rgbaArrayToInt(rgba);

            for (int color: backgroundColor) {
                if (pixel == color) {skip = true; break;}
            }

            if (skip) return rgba;

            int minDist = Integer.MAX_VALUE;
            Blob nearestBlob = null;

            for (Blob blob: blobList) {
                int dist = blob.squareDistance(x, y);
                if (dist < minDist) {
                    minDist = dist;
                    nearestBlob = blob;
                }
            }

            if (minDist < distanceThreshold*distanceThreshold) {
                nearestBlob.add(x, y);
            } else {
                blobList.add(new Blob(x, y));
            }

            return rgba;
        });

        return blobList;
    }

    /**
     * Detect blobs and automatically merges adjacent blobs
     * @param image Image to detect blobs
     * @param backgroundColor Background color of image to ignore
     * @param distanceThreshold Distance threshold for adding points to blobs
     * @return An arraylist of Blobs
     */
    public static List<Blob> detectBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold) {
        List<Blob> blobs = detectDiscreteBlobs(image, backgroundColor, distanceThreshold);
        mergeBlobs(blobs);

        return blobs;
    }

    public static void mergeBlobs(List<Blob> blobList) {
        //int mergeCount = 0;
        //int originalSize = blobList.size();

        int i = 0;
        while (i < blobList.size()) {
            int j = blobList.size() - 1;

            boolean merged = false;

            while (j > i) {
                Blob mainBlob = blobList.get(i); // Main blob of this loop
                Blob checkBlob = blobList.get(j); // Blob to check against

                if (mainBlob.shouldMerge(checkBlob)) {
                    blobList.set(i, new Blob(mainBlob, checkBlob));
                    blobList.remove(checkBlob);
                    merged = true;
                    //mergeCount++;
                }

                j--;
            }

            // If a merge was performed, check current blob again since it is a new blob
            if (!merged) i++;
        }
        //assert originalSize == mergeCount + blobList.size();
        //System.out.printf("BlobDetector.java: Merged %d blobs. %d -> %d\n", mergeCount, originalSize, blobList.size());
    }

    public static Point[] blobsToPoints(List<Blob> blobList) {
        List<Point> points = new ArrayList<>();

        for (Blob blob : blobList) {
            points.addAll(blob.getPoints());
        }

        return points.toArray(new Point[0]);
    }
}
