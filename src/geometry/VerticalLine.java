package geometry;

import lejos.robotics.geometry.Point;

import java.awt.*;

public class VerticalLine extends ColoredRegion {

    private final float y1;
    private final float y2;
    private final float x;
    private final float weight;

    public VerticalLine(int color, float x, float y1, float y2, float weight) {
        super(color);

        if (y1 < y2) {
            this.y1 = y1;
            this.y2 = y2;
        } else {
            this.y1 = y2;
            this.y2 = y1;
        }

        this.x = x;
        this.weight = weight;
    }

    @Override
    public boolean contains(Point point) {
        return y1 < point.y && point.y < y2 && x - weight / 2 < point.x && point.x < x + weight / 2;
    }

    @Override
    void drawRegion(Graphics g) {
        g.fillRect((int) (x -weight), (int) y1, (int) (x+weight), (int) y2);
    }
}
