package geometry;

import PC.GUI;
import lejos.robotics.geometry.Point;

import java.awt.*;

class Rectangle extends ColoredRegion {

    private final float x1;
    private final float y1;
    private final float h;
    private final float w;

    Rectangle(int color, float x1, float y1, float w, float h) {
        super(color);
        this.x1 = x1;
        this.y1 = y1;
        this.h = h;
        this.w = w;
    }

    @Override
    void drawRegion(Graphics g) {
        g.fillRect(GUI.adjustSize(x1), GUI.adjustSize(y1), GUI.adjustSize(w), GUI.adjustSize(h));
    }

    @Override
    boolean contains(Point point) {
        return x1 < point.getX() && point.getX() < x1 + w && y1 < point.getY() && point.getY() < y1 + h;
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getHeight() {
        return h;
    }

    public float getWidth() {
        return w;
    }
}
