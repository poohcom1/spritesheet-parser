package com.poohcom1.spritesheetparser.window.spriteplayer;

import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteSequence;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;

import javax.swing.*;
import java.awt.*;

public class SpritePlayer extends JPanel {
    private SpriteCanvas canvas;

    private SpriteSequence sprites;

    private int framerate;

    public SpritePlayer(SpriteSequence sprite) {
        setSprites(sprite);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());

        this.framerate = 12;
        canvas = new SpriteCanvas(sprites, SpriteUtil.fpsFromMs(framerate));

        JPanel optionsPanel = new JPanel();

        mainPanel.add(canvas, BorderLayout.NORTH);
        //mainPanel.add(optionsPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public void setSprites(SpriteSequence sprites) {this.sprites = sprites;}

    public void setFramerate(int framerate) {this.framerate = framerate;}
}