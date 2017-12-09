package geometry;

import lejos.robotics.geometry.Point;

import java.awt.*;

@Deprecated
public class HorizontalLine extends ColoredRegion {
    private final float x1;
    private final float x2;
    private final float y;
    private final float weight;

    public HorizontalLine(int color, float x1, float x2, float y, float weight) {
        super(color);

        if (x2 > x1) {
            this.x2 = x1;
            this.x1 = x2;
        } else {
            this.x1 = x1;
            this.x2 = x2;
        }

        this.y = y;
        this.weight = weight;
    }

    @Override
    public boolean contains(Point point) {
        return x1 < point.x && point.x < x2 && y - weight < point.y && point.y < y + weight;
    }

    @Override
    void drawRegion(Graphics g) {
        g.fillRect((int) x1, (int) (y + weight), (int) x2, (int) (y - weight));
    }
}
