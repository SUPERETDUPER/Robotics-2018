/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import com.snatik.polygon.Polygon;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Any colored region that is a polygon
 */
class IrregularPolygon extends ColorRegion {

    private final Polygon polygon;

    IrregularPolygon(Color color, @NotNull List<Point> vertexes) {
        super(color);

        Polygon.Builder builder = Polygon.Builder();

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
}
