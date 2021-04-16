package com.poohcom1.spritesheetparser.util.Shapes2D;

public class Point extends java.awt.Point {

    public Point(int x, int y) {
        super(x, y);
    }

    public Point add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Point add(Point point) {
        this.x += point.x;
        this.y += point.y;
        return this;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
