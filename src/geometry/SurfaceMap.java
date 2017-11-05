package geometry;

import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Rectangle;

import java.util.ArrayList;

public class SurfaceMap {
    private static final int DEFAULT_COLOR = Color.WHITE;

    private ArrayList<ColoredRegion> regions = new ArrayList<>();

    private Rectangle boundingRectangle;

    public SurfaceMap(Rectangle boundingRectangle) {
        this(boundingRectangle, new ArrayList<ColoredRegion>());
    }

    public SurfaceMap(Rectangle boundingRectangle, ArrayList<ColoredRegion> regions) {
        this.boundingRectangle = boundingRectangle;
        this.regions = regions;
    }

    public boolean contains(Point point) {
        return boundingRectangle.contains(point);
    }

    public void addRegion(ColoredRegion coloredRegion) {
        regions.add(coloredRegion);
    }

    public int colorAtPoint(Point point) {
        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                return region.getColor();
            }
        }
        return DEFAULT_COLOR;
    }
}