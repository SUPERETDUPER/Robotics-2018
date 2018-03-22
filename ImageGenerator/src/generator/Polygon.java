/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Any colored region that is a polygon
 */
class Polygon extends ColorRegion {

    private final com.snatik.polygon.Polygon polygon;
    private final List<Point> vertexes;

    Polygon(Color color, List<Point> vertexes) {
        super(color);

        this.vertexes = vertexes;

        com.snatik.polygon.Polygon.Builder builder = com.snatik.polygon.Polygon.Builder();

        for (Point currentPoint : vertexes) {
            builder.addVertex(new com.snatik.polygon.Point(currentPoint.x, currentPoint.y));
        }

        polygon = builder.build();
    }

    // Finds polygon by number of intersections check
    @Override
    public boolean contains(float x, float y) {
        return polygon.contains(new com.snatik.polygon.Point(x, y));
    }

    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        super.displayOnGui(g);
        double[] xValues = new double[vertexes.size()];
        double[] yValues = new double[vertexes.size()];

        for (int i = 0; i < vertexes.size(); i++) {
            xValues[i] = vertexes.get(i).x;
            yValues[i] = vertexes.get(i).y;
        }

        g.fillPolygon(xValues, yValues, vertexes.size());
    }
}
