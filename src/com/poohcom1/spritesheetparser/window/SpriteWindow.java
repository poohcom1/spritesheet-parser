package com.poohcom1.spritesheetparser.window;

import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;

import javax.swing.*;
import java.awt.*;

public class SpriteWindow {
    private final SpriteCanvas canvas;

    private final Sprite[] sprites;

    private int framerate;

    public SpriteWindow(Sprite[] sprites, int framerate) {
        this.sprites = sprites;

        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.framerate = framerate;
        canvas = new SpriteCanvas(sprites, SpriteUtil.fpsFromMs(framerate));

        JPanel optionsPanel = new JPanel();

        mainPanel.add(canvas, BorderLayout.NORTH);
        //mainPanel.add(optionsPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

}