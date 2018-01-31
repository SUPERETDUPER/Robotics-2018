package Common.mapping;

import Common.utils.Logger;
import PC.GUI.Displayable;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;

import java.awt.*;
import java.util.Arrays;

public class SurfaceMap implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 2362, 1143);

    private static final java.util.List<? extends ColoredRegion> regions = Arrays.asList(
            new Rectangle(Color.BLACK, 0, 561, 1214, 20),
            new Rectangle(Color.BLACK, 1170, 0, 20, 753),

            new FlatLine(Color.BLACK, 1776, 0, 814, 29, 225),
            new FlatLine(Color.BLACK, 1776, 1143, 814, 29, 135),

            new Rectangle(Color.GREEN, 1031, 0, 300, 300)
    );

    public static boolean contains(Point point) {
        return boundingRectangle.contains(point);
    }

    public static int colorAtPoint(Point point) {
        if (!contains(point)) {
            Logger.warning(LOG_TAG, "Point out of bounds");
        }

        int colorUnderPoint = boundingRectangle.getColor();
        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                colorUnderPoint = region.getColor();
            }
        }

        return colorUnderPoint;
    }

    public static Point getRandomPoint() {
        return new Point(
                boundingRectangle.getX1() + (float) (Math.random()) * boundingRectangle.getWidth(),
                boundingRectangle.getY1() + (float) (Math.random()) * boundingRectangle.getHeight()
        );
    }

    public void displayOnGui(Graphics g) {
        boundingRectangle.setDisplayColor(g);
        boundingRectangle.drawRegion(g);

        for (ColoredRegion region : regions) {
            region.setDisplayColor(g);
            region.drawRegion(g);
        }
    }
}