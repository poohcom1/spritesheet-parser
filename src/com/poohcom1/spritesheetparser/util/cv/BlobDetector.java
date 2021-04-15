package com.poohcom1.spritesheetparser.util.cv;
import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlobDetector {
    public static final int LEFT_TO_RIGHT = 0;
    public static final int TOP_TO_BOTTOM = 1;
    public static final int RIGHT_TO_LEFT = 2;
    public static final int BOTTOM_TO_TOP = 3;

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
        blobs.sort((a, b) -> a.compareTo(b, LEFT_TO_RIGHT, TOP_TO_BOTTOM));

        return blobs;
    }

    public static List<Blob> detectBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold, int primaryOrder, int secondaryOrder) {
        List<Blob> blobs = detectDiscreteBlobs(image, backgroundColor, distanceThreshold);
        mergeBlobs(blobs);
        blobs.sort((a, b) -> a.compareTo(b, primaryOrder, secondaryOrder));

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