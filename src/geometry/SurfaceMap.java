package geometry;

import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import utils.Logger;

import java.awt.*;
import java.util.Arrays;

public class SurfaceMap {

    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 236.2F, 114.3F);

    private static java.util.List<? extends ColoredRegion> regions = Arrays.asList(
            new Rectangle(Color.BLACK, 0, 56.1F, 121.4F, 2),
            new Rectangle(Color.BLACK, 117, 0, 2, 75.3F),

            new FlatLine(Color.BLACK, 177.6F, 0, 81.4F, 2.9F, 225),
            new FlatLine(Color.BLACK, 177.6F, 114.3F, 81.4F, 2.9F, 135),

            new Rectangle(Color.GREEN, 103.1F, 0, 30, 30)
    );

    public static void paintComponent(Graphics g) {

        boundingRectangle.setDisplayColor(g);
        boundingRectangle.drawRegion(g);

        for (ColoredRegion region : regions) {
            region.setDisplayColor(g);
            region.drawRegion(g);
        }
    }

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

    public static Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }
}