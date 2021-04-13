package com.poohcom1.spritesheetparser.util;

import java.awt.*;

public class Rect extends Rectangle {

    public Rect(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2 - x1, y2 - y1);
    }

    public Rect(Point min, Point max) {
        super(min.x, min.y, max.x - min.x, max.y - min.y);
    }

    public boolean touches(Rect other) {
        if (x < other.x + other.width && x + width > other.x) {
            return y == other.y + other.height || other.y == y + height ||
                    y == other.y + other.height + 1 || other.y == y + height + 1;
        }

        if (y < other.y + other.height && y + height > other.y) {
            return x == other.x + other.width || other.x == x + width ||
                    x == other.x + other.width + 1 || other.x == x + width + 1;
        }

        return false;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + (x + width) + ", " + (y + height) + ")";
    }
}
