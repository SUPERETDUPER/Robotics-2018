/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

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
    public void displayOnGui(@NotNull GraphicsContext g) {
        g.setFill(getDisplayColor(null));
    }
}
