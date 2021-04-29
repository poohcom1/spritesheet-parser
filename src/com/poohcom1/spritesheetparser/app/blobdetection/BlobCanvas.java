package com.poohcom1.spritesheetparser.app.blobdetection;

import com.poohcom1.spritesheetparser.app.reusables.ToolsCanvas;
import com.poohcom1.spritesheetparser.util.cv.Blob;
import com.poohcom1.spritesheetparser.util.cv.BlobSequence;
import com.poohcom1.spritesheetparser.util.shapes2D.Rect;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlobCanvas extends ToolsCanvas {
    // Tools
    public final static String MERGE_TOOL = "Merge sprite boxes";
    public final static String DELETE_TOOL = "Delete sprite box";
    public final static String CUT_TOOL = "Remove pixels";

    // Options
    private boolean _showBlobs = true;
    private boolean _showPoints = true;
    private boolean _showNumbers = true;
    private Color _blobColor = Color.RED;
    private Color _pointColor = new Color(0, 0, 255, 104);


    // Objects
    private BufferedImage image;
    private List<Blob> blobs;

    public BlobCanvas(BufferedImage image) {
        super(image.getWidth(), image.getHeight());

        maxMarqueeCount = -1;

        setImage(image);
        this.blobs = new ArrayList<>();

        addTool(MOVE_TOOL, moveToolCallback);

        addTool(MERGE_TOOL, new MarqueeAdapter() {
            @Override
            protected void endMarquee(List<Rect> marquees, Point pos) {
                if (marquees.size() > 0) {
                    Rect marquee = getTrueMarqueesCoords().get(0);

                    List<Blob> foundBlob = new ArrayList<>();

                    blobs.forEach(blob -> {
                        if (marquee.contains(blob)) {
                            foundBlob.add(blob);
                        }
                    });

                    ((BlobSequence) blobs).mergeBlobs(foundBlob);
                }
                marquees.clear();
                notifyUpdateListeners();
            }
        });

        addTool(DELETE_TOOL, new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                deleteBlobAtPoint(getPositionOnImage(e.getPoint()));
            }
            public void mouseDragged(MouseEvent e) {
                parentPanel.setMouseMove(false);
                deleteBlobAtPoint(getPositionOnImage(e.getPoint()));
            }
            private void deleteBlobAtPoint(Point point) {
                for (int i = blobs.size()-1; i >= 0; i--) {
                    Blob blob = blobs.get(i);
                    if (blob.contains(point) || (blob.x == point.x && blob.y == point.y)) {
                        blobs.remove(i);
                    }
                }
                notifyUpdateListeners();
            }
        });

        addTool(CUT_TOOL, new MarqueeAdapter() {
            @Override
            protected void endMarquee(List<Rect> marquees, Point pos) {
                if (marquees.size() > 0) {
                    Rect marquee = getTrueMarqueesCoords().get(0);

                    for (int i = blobs.size()-1; i >= 0; i--) {
                        Blob blob = blobs.get(i);

                        // If blob is in marquee
                        if (marquee.intersects(blob)) {

                            for (int j = blob.getPoints().size() - 1; j >= 0; j--) {
                                Point point = blob.getPoints().get(j);
                                // Remove points if within marquee
                                if (marquee.contains(point)) {
                                    // Remove point
                                    blob.removePoint(point);

                                    if (blob.width == 0 && blob.height == 0) {
                                        blobs.remove(blob);
                                        break;
                                    }
                                }
                            }
                        } else if (marquee.contains(blob)) {
                            blobs.remove(blob);
                        }

                        if (blob.getPoints().size() == 0) {
                            blobs.remove(blob);
                        }
                    }
                }

                marquees.clear();
                notifyUpdateListeners();
            }
        });
    }

    public List<Blob> getBlobs() {
        return blobs;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Stroke defaultStroke = ((Graphics2D) g).getStroke();

        int xOffset = getXOffset();
        int yOffset = getYOffset();

        g.drawImage(image, xOffset, yOffset, null);

        if (_showBlobs || _showNumbers) {
            for (int i = 0; i < blobs.size(); i++) {
                Rect rect = blobs.get(i);

                if (_showBlobs) {
                    g.setColor(_blobColor);
                    ((Graphics2D) g).setStroke(new BasicStroke(
                            (float) (1.5f / xScale),                      // Width
                            BasicStroke.CAP_SQUARE,    // End cap
                            BasicStroke.JOIN_BEVEL,    // Join style
                            1.0f,                     // Miter limit
                            new float[]{2.0f, 2.0f},          // Dash pattern
                            0.1f));
                    g.drawRect(rect.x + xOffset, rect.y + yOffset, rect.width + 1, rect.height + 1);
                }

                if (_showNumbers) {

                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(i), rect.x + rect.width + xOffset, rect.y + rect.height + yOffset);
                }
            }
        }

        if (_showPoints) {
            for (Point point : ((BlobSequence) blobs).toPoints()) {
                g.setColor(_pointColor);
                g.drawRect(point.x + xOffset, point.y + yOffset, 1, 1);
            }
        }

        drawMarquees(g);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        setSize(image.getWidth(), image.getHeight());
    }

    public void setBlobs(java.util.List<Blob> blobs) {
        this.blobs = blobs;
    }

    public void setShowBlobs(boolean showBlobs) {
        _showBlobs = showBlobs;
        repaint();
    }

    public void setShowNumbers(boolean showNumbers) {
        _showNumbers = showNumbers;
        repaint();
    }

    public void setShowPoints(boolean showPoints) {
        _showPoints = showPoints;
        repaint();
    }

    public boolean isShowingBlobs() {
        return _showBlobs;
    }

    public boolean isShowingPoints() {
        return _showPoints;
    }

    public boolean isShowingNumbers() {
        return _showNumbers;
    }
}
