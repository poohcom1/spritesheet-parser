package com.poohcom1.spritesheetparser.window.spriteplayer;

import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;
import com.poohcom1.spritesheetparser.window.ZoomableCanvas;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpriteCanvas extends ZoomableCanvas {
    private Sprite[] sprites;
    private float msPerFrame;

    private int _frame = 0;
    private boolean _isPlaying = true;

    private final ScheduledExecutorService animator;

    public SpriteCanvas(Sprite[] sprites, float msPerFrame) {
        this.sprites = sprites;
        this.msPerFrame = msPerFrame;

        setSize(SpriteUtil.spriteMaxDimension(sprites));

        System.out.println(SpriteUtil.spriteMaxDimension(sprites));

        animator = Executors.newScheduledThreadPool(1);

        animator.scheduleAtFixedRate(() -> {
            if (_isPlaying) {
                _frame++;
                if (_frame > sprites.length-1) {
                    _frame = 0;
                }
                repaint();
            }
        }, 0, (long) msPerFrame, TimeUnit.MILLISECONDS);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension original = SpriteUtil.spriteMaxDimension(sprites);
        return new Dimension((int) (original.getWidth() * xScale), (int) (original.getHeight() * yScale));
    }

    public void play() {
        _isPlaying = true;
    }

    public void pause() {
        _isPlaying = false;
    }

    @Override
    protected void paintChildren(Graphics graphics) {
        Graphics2D g = zoomedGraphic(graphics);

        g.drawImage(sprites[_frame].getSprite(), 0, 0, null);
    }
}
