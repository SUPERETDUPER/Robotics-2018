package Common.mapping;

import Common.utils.Logger;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

/*
Lines who's ends are horizontal
 */
class FlatLine extends SingleColorRegion {
    private static final String LOG_TAG = FlatLine.class.getSimpleName();

    @NotNull
    private final Polygon flatLine;

    FlatLine(int color, float x1, float y1, float length, float hWidth, float angle) {
        super(color);

        if (angle > 90 || angle < 0) {
            Logger.warning(LOG_TAG, "Only tested with angles between 0 and 90");
        }

        float x2 = (float) (x1 + Math.cos(Math.toRadians(angle)) * length);
        float y2 = (float) (y1 - Math.sin(Math.toRadians(angle)) * length);

        flatLine = new Polygon(color, Arrays.asList(
                new Point(x1, y1),
                new Point(x1 + hWidth, y1),
                new Point(x2 + hWidth, y2),
                new Point(x2, y2)
        ));
    }

    @Override
    public void displayOnGui(@NotNull Graphics g) {
        super.displayOnGui(g);
        flatLine.displayOnGui(g);
    }

    @Override
    public boolean contains(@NotNull Point point) {
        return flatLine.contains(point);
    }
}
