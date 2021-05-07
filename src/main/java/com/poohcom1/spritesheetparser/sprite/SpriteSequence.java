package com.poohcom1.spritesheetparser.sprite;

import com.poohcom1.spritesheetparser.cv.Blob;
import com.poohcom1.spritesheetparser.cv.BlobSequence;
import com.poohcom1.spritesheetparser.shapes2D.Rect;
import com.poohcom1.spritesheetparser.shapes2D.ShapesUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteSequence extends ArrayList<Sprite> {
    private final BlobSequence blobSequence;
    private final int backgroundColor;

    public SpriteSequence(BufferedImage spriteSheet, BlobSequence blobSequence, int backgroundColor) {
        this.blobSequence = blobSequence;
        this.backgroundColor = backgroundColor;

        addAll(extractBlobSprites(spriteSheet, blobSequence, getDimensions()));
    }

    /**
     * Extract the maximum dimension in one direction, and the
     */
    public Dimension getDimensions() {
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

    public List<BufferedImage> getImages() {
        List<BufferedImage> images = new ArrayList<>();
        forEach(sprite -> images.add(sprite.getSprite(backgroundColor)));
        return images;
    }

    public BufferedImage getImage(int index) {
        return get(index).getSprite(backgroundColor);
    }

    /**
     * Extracts sprites from a buffered image using blobs, making sure to apply the correct paddings based on the
     * blobs' ordering
     * @param image Original image to extract the sprites from
     * @param blobSequence Blob sequence used to extract the sprites
     * @param spriteDimension The dimensions of the sprite sequence
     */
    public static List<Sprite> extractBlobSprites(BufferedImage image, BlobSequence blobSequence, Dimension spriteDimension) {
        if (blobSequence.size() <= 0) return new ArrayList<>();

        List<Sprite> spriteList = new ArrayList<>();

        blobSequence.orderBlobs();

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

                BufferedImage subImage = image.getSubimage(blob.x, blob.y,
                        blob.width,
                        blob.height);

                Sprite spr = new Sprite(subImage, xOffset, yOffset, spriteDimension.width, spriteDimension.height);

                spriteList.add(spr);
            }
        }

        return spriteList;
    }

}
