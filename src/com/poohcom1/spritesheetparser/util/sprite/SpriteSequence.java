package com.poohcom1.spritesheetparser.util.sprite;

import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

public class SpriteSequence implements Iterable<Sprite>{
    private final BufferedImage spriteSheet;
    private final BlobSequence blobs;

    private List<Sprite> spriteSequence;

    public SpriteSequence(BufferedImage spriteSheet, BlobSequence blobs) {
        this.spriteSheet = spriteSheet;
        this.blobs = blobs;

        spriteSequence = SpriteUtil.extractBlobSprites(spriteSheet, blobs);
    }

    private void alignSprites(int baseline) {

    }

    @Override
    public Iterator<Sprite> iterator() {
        return spriteSequence.iterator();
    }
}
