package com.poohcom1.spritesheetparser.util.cv;
import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.Rect;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlobDetector {
    public BlobDetector() {}

    public static ArrayList<Blob> detectDiscreteBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold) {
        int width = image.getWidth();
        int height = image.getHeight();

        ArrayList<Blob> blobList = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = false;

                int pixel = image.getRGB(x, y);

                for (int color: backgroundColor) {
                    if (pixel == color) {skip = true; break;}
                }

                if (skip) continue;

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
            }
        }

        return blobList;
    }

    public static ArrayList<Blob> detectBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold) {
        ArrayList<Blob> blobs = detectDiscreteBlobs(image, backgroundColor, distanceThreshold);
        mergeBlobs(blobs);

        return blobs;
    }

    public static void mergeBlobs(ArrayList<Blob> blobList) {
        //int mergeCount = 0;
        //int originalSize = blobList.size();

        int i = 0;
        while (i < blobList.size()) {
            int j = blobList.size() - 1;

            boolean merged = false;

            while (j > i) {
                Blob mainBlob = blobList.get(i);
                Blob checkBlob = blobList.get(j);

                if (mainBlob.isTouching(checkBlob)) {
                    blobList.set(i, mainBlob.merge(checkBlob));
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

    public static Rect[] blobsToRect(ArrayList<Blob> blobList) {
        Rect[] boxes = new Rect[blobList.size()];

        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = blobList.get(i).toRect();
        }

        return boxes;
    }

    public static Point[] blobsToPoints(ArrayList<Blob> blobList) {
        ArrayList<Point> points = new ArrayList<>();

        for (Blob blob : blobList) {
            points.addAll(blob.toPoints());
        }

        return points.toArray(new Point[0]);
    }
}