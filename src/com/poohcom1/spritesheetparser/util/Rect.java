package com.poohcom1.spritesheetparser.util;

public class Rect {
    public int x1;
    public int x2;
    public int y1;
    public int y2;

    public Rect(int x1, int y1, int x2, int y2) {
        storePoints(x1, y1, x2, y2);
    }

    public Rect(Point min, Point max) {
        storePoints(min.x, min.y, max.x, max.y);
    }

    private void storePoints(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }

        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }

        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public Point min() {
        return new Point(x1, y1);
    }

    public Point max() {
        return new Point(x2, y2);
    }

    public int getWidth() {
        return x2 - x1;
    }

    public int getHeight() {
        return y2 = y1;
    }

    public String toString() {
        return new Point(x1, y1).toString() + ", " + new Point(x2, y2).toString();
    }
}
