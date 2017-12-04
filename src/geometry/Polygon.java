package geometry;

import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;

public class Polygon extends ColoredRegion {

    private final ArrayList<Point> points;
    private float maxX;

    public Polygon(int color, ArrayList<Point> points) {
        super(color);

        this.points = points;

        maxX = this.points.get(0).x;

        for (Point2D.Float point : this.points) {
            if (maxX < point.x) {
                maxX = point.x;
            }
        }
    }

    @Override
    public boolean contains(Point point) {
        Line hLine = new Line(point.x, point.y, maxX + 1, point.y);

        int intersections = 0;

        Point previousPoint = points.get(points.size() - 1);

        for (Point currentPoint : points) {

            Line edge = new Line(previousPoint.x, previousPoint.y, currentPoint.x, currentPoint.y);

            if (hLine.intersectsLine(edge)) {
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

        for (int i = 0; i < points.size() ; i++){
            xValues[i] =(int) points.get(i).x;
            yValues[i] = (int) points.get(i).y;
        }
        g.fillPolygon(xValues, yValues, points.size());
    }
}
