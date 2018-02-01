package Common.mapping;

import lejos.robotics.geometry.Point;

import java.awt.*;
import java.util.List;

public class RegionContainer implements ColoredRegion {
    private static final String LOG_TAG = RegionContainer.class.getSimpleName();

    private final List<? extends ColoredRegion> regions;

    RegionContainer(List<? extends ColoredRegion> regions) {
        this.regions = regions;
    }

    @Override
    public void displayOnGui(Graphics g) {
        for (ColoredRegion region : regions) {
            region.displayOnGui(g);
        }
    }

    @Override
    public int getColorAtPoint(Point point) {
        int colorUnderPoint = -1;

        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                colorUnderPoint = region.getColorAtPoint(point);
            }
        }

        return colorUnderPoint;
    }

    @Override
    public boolean contains(Point point) {
        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                return true;
            }
        }
        return false;
    }
}
