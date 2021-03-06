package com.poohcom1.spritesheetparser.shapes2D;

import java.awt.*;

public class Rect extends Rectangle {
    public final static int HORIZONTAL_AXIS = 0;
    public final static int VERTICAL_AXIS = 1;

    public final static int LEFT = 0;
    public final static int TOP = 1;
    public final static int RIGHT = 2;
    public final static int BOTTOM = 3;

    private Point anchor;

    public Rect() {}

    public Rect(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2 - x1, y2 - y1);
    }

    public Rect(Point min, Point max) {
        super(min.x, min.y, max.x - min.x, max.y - min.y);
    }

    public Rect(Rectangle rectangle) {
        super(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }


    public void setMaxX(int x) {
        if (x < this.x) throw new IllegalArgumentException("Max x cannot be less than x!");
        width = x - this.x;
    }

    public void setMaxY(int y) {
        if (y < this.y) throw new IllegalArgumentException("Max y cannot be less than y!");
        height = y - this.y;
    }

    public Point getMin() {return new Point(x, y);}

    public Point getMax() {return new Point(x + width, y + height);}

    public int maxX() {
        return x + width;
    }

    public int maxY() {
        return y + height;
    }

    public int centerX() {return (x + (width/2));}

    public int centerY() {return (y + (height/2));}

    public boolean overlapsDirection(Rect other, int axis) {
        switch (axis) {
            case HORIZONTAL_AXIS -> {
                return (x <= other.x + other.width && x + width >= other.x);
            }
            case VERTICAL_AXIS -> {
                return (y <= other.y + other.height && y + height >= other.y);
            }
        }
        return false;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + (x + width) + ", " + (y + height) + ")";
    }

    public int[] getSides() {
        return new int[] {x, y, x + width, y + height};
    }

    public void setAnchor() {
        this.anchor = new Point(x, y);
    }

    public void setAnchor(Point point) {setAnchor(point.x, point.y);}

    public void setAnchor(int x, int y) {
        this.anchor = new Point(x, y);
    }

    public void resizeWithAnchor(Point point) {
        resizeWithAnchor(point.x, point.y);
    }

    public void resizeWithAnchor(int x, int y) {
        if (anchor == null) {
            throw new Error("No anchors set.");
        }

        // Compare x
        if (x > anchor.x) {
            this.x = anchor.x;
            setMaxX(x);
        } else {
            this.x = x;
            setMaxX(anchor.x);
        }

        if (y > anchor.y) {
            this.y = anchor.y;
            setMaxY(y);
        } else {
            this.y = y;
            setMaxY(anchor.y);
        }
    }

    /**
     * Checks whether or not this Rectangle entirely contains the specified Rectangle, including Rectangles of size 0.
     * @param r Rectangle to check whether or not this Rectangle contains
     * @return Whether or not the given Rectangle is contained in this Rectangle
     */
    @Override
    public boolean contains(Rectangle r) {
        return r.x >= x && r.x+r.width <= x + width && r.y >= y && r.y + r.height <= y + height;
    }

    @Override
    public boolean contains(Point p) {
        return (p.x > x-1 && p.x < x + width && p.y > y-1 && p.y < y + height);
    }

    @Override
    public boolean intersects(Rectangle r) {
        int tw = this.width;
        int th = this.height;
        int rw = r.width;
        int rh = r.height;

        int tx = this.x;
        int ty = this.y;
        int rx = r.x;
        int ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    public boolean intersectTouch(Rectangle r) {
        int tw = this.width;
        int th = this.height;
        int rw = r.width;
        int rh = r.height;

        int tx = this.x;
        int ty = this.y;
        int rx = r.x;
        int ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw <= rx || rw >= tx) &&
                (rh <= ry || rh >= ty) &&
                (tw <= tx || tw >= rx) &&
                (th <= ty || th >= ry));
    }

    public boolean touches(Rect other) {
        if (x < other.x + other.width && x + width > other.x) {
            return y == other.y + other.height || other.y == y + height;
        }

        if (y < other.y + other.height && y + height > other.y) {
            return x == other.x + other.width || other.x == x + width;
        }

        return false;
    }
}
