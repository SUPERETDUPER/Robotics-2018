package geometry;

import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Point2D;

import java.awt.*;
import java.util.List;

class Polygon extends ColoredRegion {

    private final java.util.List<Point> points;
    private float maxX;

    Polygon(int color, List<Point> points) {
        super(color);

        this.points = points;

        maxX = this.points.get(0).x;

        for (Point2D.Float point : this.points) {
            if (point.x > maxX) {
                maxX = point.x;
            }
        }
    }

    // Finds polygon by number of intersections check
    @Override
    boolean contains(Point point) {
        Line hLine = new Line(point.x, point.y, maxX + 1, point.y);

        int intersections = 0;

        Point previousPoint = points.get(points.size() - 1);

        for (Point currentPoint : points) {

            Line previousToCurrentEdge = new Line(previousPoint.x, previousPoint.y, currentPoint.x, currentPoint.y);

            if (hLine.intersectsLine(previousToCurrentEdge)) {
                intersections += 1;
            }

            previousPoint = currentPoint;
        }

        return intersections % 2 == 0;
    }

    @Override
    void drawRegion(Graphics g) {
        int[] xValues = new int[points.size()];
        int[] yValues = new int[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xValues[i] = (int) points.get(i).x;
            yValues[i] = (int) points.get(i).y;
        }

        g.fillPolygon(xValues, yValues, points.size());
    }
}
