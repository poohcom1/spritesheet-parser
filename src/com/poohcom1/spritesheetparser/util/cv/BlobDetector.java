package com.poohcom1.spritesheetparser.util.cv;

import com.poohcom1.spritesheetparser.util.Rect;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlobDetector {
    public BlobDetector() {}

    public static Rect[] detectBlobs(BufferedImage image, int[] backgroundColor, int distanceThreshold) {
        int width = image.getWidth();
        int height = image.getHeight();

        ArrayList<Blob> blobList = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean skip = false;

                int pixel = image.getRGB(x, y);

                for (int color: backgroundColor) {
                    if (pixel == color) {skip = true; break;}
                }

                if (skip) continue;

                int minDist = width + height;
                Blob nearestBlob = null;

                for (Blob blob: blobList) {
                    int dist = blob.squareDistance(x, y);
                    if (dist < minDist) {
                        minDist = dist;
                        nearestBlob = blob;
                    }
                }

                if (minDist < distanceThreshold) {
                    nearestBlob.add(x, y);
                } else {
                    blobList.add(new Blob(x, y));
                }
            }
        }

        Rect[] boxes = new Rect[blobList.size()];

        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = blobList.get(i).toRect();
        }

        return boxes;
    }

}