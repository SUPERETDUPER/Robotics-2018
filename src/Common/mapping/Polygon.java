/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package Common.mapping;

import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/*
Any type of polygon
 */
class Polygon extends SingleColorRegion {

    private final List<Point> points;
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
    public void displayOnGui(@NotNull Graphics g) {
        super.displayOnGui(g);
        int[] xValues = new int[points.size()];
        int[] yValues = new int[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xValues[i] = (int) points.get(i).x;
            yValues[i] = (int) points.get(i).y;
        }

        g.fillPolygon(xValues, yValues, points.size());
    }
}
