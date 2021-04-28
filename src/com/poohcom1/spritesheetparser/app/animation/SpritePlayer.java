package com.poohcom1.spritesheetparser.app.animation;

import com.poohcom1.spritesheetparser.app.reusables.ZoomComponent;
import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteSequence;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpritePlayer extends ZoomComponent {
    private List<Sprite> sprites;
    private long msPerFrame;

    private int _frame = 0;
    private boolean _isPlaying = true;

    private ScheduledExecutorService animator;

    public SpritePlayer(SpriteSequence sprites, long msPerFrame) {
        super(sprites.getDimensions().width, sprites.getDimensions().height);
        this.sprites = sprites;
        this.msPerFrame = msPerFrame;

        setSize(sprites.getDimensions());

        System.out.println(sprites.getDimensions());

        animator = Executors.newScheduledThreadPool(1);

        animator.scheduleAtFixedRate(animationCallback, 0, msPerFrame, TimeUnit.MILLISECONDS);
    }

    private final Runnable animationCallback = (Runnable) () -> {
        if (_isPlaying) {
            _frame++;
            if (_frame > sprites.size()-1) {
                _frame = 0;
            }
            repaint();
        }
    };

    public void setSprites(List<Sprite> sprites) {
        animator.shutdown();
        _frame = 0;
        this.sprites = sprites;
        animator = Executors.newScheduledThreadPool(1);
        animator.scheduleAtFixedRate(animationCallback, 0, msPerFrame, TimeUnit.MILLISECONDS);
    }

    public void setMsPerFrame(long msPerFrame) {
        this.msPerFrame = msPerFrame;
    }

    public void play() {
        _isPlaying = true;
    }

    public void pause() {
        _isPlaying = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sprites.size() > 0) g.drawImage(sprites.get(_frame).getSprite(), 0, 0, null);
    }
}
