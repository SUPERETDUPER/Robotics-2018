package Common.mapping;

import Common.Logger;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public abstract class SingleColorRegion implements ColoredRegion {

    private static final String LOG_TAG = ColoredRegion.class.getSimpleName();

    private final int mColor;

    SingleColorRegion(int color) {
        this.mColor = color;
    }

    public int getColorAtPoint(Point point) {
        return mColor;
    }

    @Nullable
    private java.awt.Color getDisplayColor() {
        switch (mColor) {
            case Color.BLACK:
                return java.awt.Color.BLACK;
            case Color.WHITE:
                return java.awt.Color.WHITE;
            case Color.BLUE:
                return new java.awt.Color(0, 117, 191);
            case Color.BROWN:
                return java.awt.Color.DARK_GRAY;
            case Color.GREEN:
                return new java.awt.Color(0, 172, 70);
            case Color.RED:
                return new java.awt.Color(237, 28, 36);
            case Color.YELLOW:
                return new java.awt.Color(255, 205, 3);
            default:
                Logger.warning(LOG_TAG, "Region not a valid color");
                return null;
        }
    }

    @Override
    public void displayOnGui(@NotNull Graphics g) {
        g.setColor(getDisplayColor());
    }
}
