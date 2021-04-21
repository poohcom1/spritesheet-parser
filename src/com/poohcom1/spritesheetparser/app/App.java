package com.poohcom1.spritesheetparser.app;

import com.poohcom1.spritesheetparser.app.blobdetection.BlobCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

public class App {
    private BlobCanvas blobCanvas;

    public App() throws IOException {
        JFrame window = new JFrame("Sprite Sheet Animator");

        BlobCanvas blobCanvas = new BlobCanvas(AppUtil.loadImage("src/com/poohcom1/spritesheetparser/assets/tarmaSheet1.png"));

        ZoomablePanel zoomBlobPanel = new ZoomablePanel(blobCanvas);

        window.add(zoomBlobPanel);

        window.pack();
        window.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}
