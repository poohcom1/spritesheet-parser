package com.poohcom1.spritesheetparser.util.sprite;

import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.image.ImageUtil;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;
import com.poohcom1.spritesheetparser.util.shapes2D.ShapesUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpriteSequence extends ArrayList<Sprite> {
    private final BufferedImage spriteSheet;
    private final BlobSequence blobSequence;

    public Dimension dimension;

    public SpriteSequence(BufferedImage spriteSheet, BlobSequence blobSequence) {
        this.spriteSheet = spriteSheet;
        this.blobSequence = blobSequence;

        dimension = getDimensions();
        addAll(extractBlobSprites(spriteSheet, blobSequence, dimension));
    }

    private Dimension getDimensions() {
        int width;
        int height;

        if (blobSequence.getPrimaryOrder() == BlobSequence.LEFT_TO_RIGHT
                || blobSequence.getPrimaryOrder() == BlobSequence.RIGHT_TO_LEFT) {
            width = ShapesUtil.maxDimensions(blobSequence).width;
            height = blobSequence.getDimensions().height;
        } else {
            width = blobSequence.getDimensions().width;
            height = ShapesUtil.maxDimensions(blobSequence).height;
        }
        return new Dimension(width, height);
    }

    public void alignSprites(int alignment) {
        forEach(sprite -> sprite.align(alignment));
    }

    // Extracts sprites from a buffered image using blobs, making sure to apply the correct paddings based on the
    //  blobs' ordering
    public static List<Sprite> extractBlobSprites(BufferedImage image, BlobSequence blobSequence, Dimension sheetDimensions) {
        List<Sprite> spriteList = new ArrayList<>();

        for (List<Blob> blobRow: blobSequence.getRows()) {
            // Get the row relative dimensions
            Rect rowDimensions = ShapesUtil.maxBoundaries(blobRow);
            for (Blob blob: blobRow) {
                int xOffset = 0;
                int yOffset = 0;

                // Find the ordering, and get the relative width/height in that direction
                if (blobSequence.ordersHorizontally()) {
                    yOffset = blob.y - rowDimensions.y;
                } else {
                    xOffset = blob.x - rowDimensions.x;
                }

                BufferedImage subImage = image.getSubimage(blob.x, blob.y, blob.width+1, blob.height+1);

                Sprite spr = new Sprite(subImage, xOffset, yOffset, sheetDimensions.width, sheetDimensions.height);

                spriteList.add(spr);
            }
        }

        return spriteList;
    }

    public void alignSprites() {

    }
}
