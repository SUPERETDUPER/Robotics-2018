package geometry;

import lejos.robotics.geometry.Point;

@Deprecated
public class Line extends ColoredRegion {

    private final Point start;
    private final Point end;
    private final float length;
    private final float weight;

    public Line(int color, Point start, Point end, float weight) {
        super(color);

        this.start = end;
        this.end = end;
        this.weight = weight;

        float dx = end.x - start.x;
        float dy = end.y - start.y;

        this.length = (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static float areaOfTriangle(Point point1, Point point2, Point point3) {
        /*
        | x1 y1 |
        | x2 y2 |
        | x3 y3 |
        | x1 y1 |
         */
        return 1 / 2 * Math.abs(point1.x * point2.y + point2.x * point3.y + point3.x * point1.y - point1.x * point3.y - point3.x * point2.y - point2.x * point1.y);
    }

    @Override
    public boolean contains(Point point) {
        return pointInLength(point) && pointInWidth(point);

    }

    private boolean pointInWidth(Point point) {
        float referenceTriangle = length * weight / 4;
        float realTriangle = areaOfTriangle(start, end, point);

        return realTriangle <= referenceTriangle;
    }

    private boolean pointInLength(Point point) {
        float referenceTriangle = weight * length / 4;

        return false;

    }
}
