package com.poohcom1.spritesheetparser.window.testwindows;

import com.poohcom1.spritesheetparser.util.sprite.Sprite;
import com.poohcom1.spritesheetparser.util.sprite.SpriteSequence;
import com.poohcom1.spritesheetparser.util.sprite.SpriteUtil;
import com.poohcom1.spritesheetparser.window.spriteplayer.SpriteCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SpriteWindow {
    private final SpriteCanvas canvas;

    private final SpriteSequence sprites;

    private int framerate;

    public SpriteWindow(SpriteSequence sprites, int framerate) {
        this.sprites = sprites;

        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());

        this.framerate = framerate;
        canvas = new SpriteCanvas(sprites, SpriteUtil.fpsFromMs(framerate));

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case 90 -> {
                        canvas.zoomOut(0.1f);
                        canvas.repaint();
                    }
                    case 88 -> {
                        canvas.zoomIn(0.1f);
                        canvas.repaint();
                    }
                }
            }
        });

        JPanel optionsPanel = new JPanel();

        mainPanel.add(canvas, BorderLayout.NORTH);
        //mainPanel.add(optionsPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

}