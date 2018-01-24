package Robotics2018.mapping;

import Robotics2018.PC.GUI.Displayable;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import Robotics2018.utils.Logger;

import java.awt.*;
import java.util.Arrays;

public class SurfaceMap implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final SurfaceMap mSurfaceMap = new SurfaceMap();

    private final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 236.2F, 114.3F);

    private final java.util.List<? extends ColoredRegion> regions = Arrays.asList(
            new Rectangle(Color.BLACK, 0, 56.1F, 121.4F, 2),
            new Rectangle(Color.BLACK, 117, 0, 2, 75.3F),

            new FlatLine(Color.BLACK, 177.6F, 0, 81.4F, 2.9F, 225),
            new FlatLine(Color.BLACK, 177.6F, 114.3F, 81.4F, 2.9F, 135),

            new Rectangle(Color.GREEN, 103.1F, 0, 30, 30)
    );

    private SurfaceMap() {
    }

    public static SurfaceMap get() {
        return mSurfaceMap;
    }

    public void displayOnGUI(Graphics g) {

        boundingRectangle.setDisplayColor(g);
        boundingRectangle.drawRegion(g);

        for (ColoredRegion region : regions) {
            region.setDisplayColor(g);
            region.drawRegion(g);
        }
    }

    public boolean contains(Point point) {
        return boundingRectangle.contains(point);
    }

    public int colorAtPoint(Point point) {
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

    public Point getRandomPoint() {
        return new Point(
                boundingRectangle.getX1() + (float) (Math.random()) * boundingRectangle.getWidth(),
                boundingRectangle.getY1() + (float) (Math.random()) * boundingRectangle.getHeight()
        );
    }
}