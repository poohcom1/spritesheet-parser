package com.poohcom1.spritesheetparser.window.spriteplayer;

import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;

import javax.swing.*;
import java.awt.*;

public class SpritePlayer extends JPanel {
    private SpriteCanvas canvas;

    private Sprite[] sprites;

    private int framerate;

    public SpritePlayer(Sprite[] sprite) {
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

    public void setSprites(Sprite[] sprites) {this.sprites = sprites;}

    public void setFramerate(int framerate) {this.framerate = framerate;}
}