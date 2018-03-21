/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
Any colored region that is a polygon
 */
class Polygon extends SingleColorRegion {

    private final List<Point> points;
    private float maxX;

    Polygon(Color color, List<Point> points) {
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
    public boolean contains(@NotNull Point point) {
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

        return intersections % 2 == 1;
    }

    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        super.displayOnGui(g);
        double[] xValues = new double[points.size()];
        double[] yValues = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xValues[i] = points.get(i).x;
            yValues[i] = points.get(i).y;
        }

        g.fillPolygon(xValues, yValues, points.size());
    }
}
