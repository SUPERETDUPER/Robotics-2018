package geometry;

import PC.MapGUI;
import lejos.robotics.geometry.Point;

import java.awt.*;

public class Rectangle extends ColoredRegion {

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
        g.fillRect(MapGUI.adjustSize(x1), MapGUI.adjustSize(y1), MapGUI.adjustSize(w), MapGUI.adjustSize(h));
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
