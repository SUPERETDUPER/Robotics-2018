package geometry;

import lejos.robotics.geometry.Point;

import java.awt.*;
import java.util.Arrays;

class FlatLine extends ColoredRegion {

    private final Polygon flatLine;

    FlatLine(int color, float x1, float y1, float length, float hWidth, float angle) {
        super(color);

        float x2 = (float) (x1 + Math.cos(Math.toRadians(angle)) * length);
        float y2 = (float) (y1 - Math.sin(Math.toRadians(angle)) * length);

        flatLine = new Polygon(color, Arrays.asList(
                new Point(x1, y1),
                new Point(x1 + hWidth, y1),
                new Point(x2 + hWidth, y2),
                new Point(x2, y2)
        ));
    }

    @Override
    void drawRegion(Graphics g) {
        flatLine.drawRegion(g);
    }

    @Override
    boolean contains(Point point) {
        return flatLine.contains(point);
    }
}
