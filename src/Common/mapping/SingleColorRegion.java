/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import Common.Logger;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public abstract class SingleColorRegion implements ColoredRegion {

    private static final String LOG_TAG = ColoredRegion.class.getSimpleName();

    private final Color mColor;

    SingleColorRegion(Color color) {
        this.mColor = color;
    }

    @Override
    public Color getDisplayColor(Point point) {
        return mColor;
    }

    @Override
    public void displayOnGui(@NotNull Graphics g) {
        g.setColor(getDisplayColor(null));
    }
}
