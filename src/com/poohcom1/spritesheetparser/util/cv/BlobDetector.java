package com.poohcom1.spritesheetparser.util.cv;
import com.poohcom1.spritesheetparser.util.Point;
import com.poohcom1.spritesheetparser.util.Rect;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlobDetector {
    public BlobDetector() {}

    public static ArrayList<Blob> detectBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold) {
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