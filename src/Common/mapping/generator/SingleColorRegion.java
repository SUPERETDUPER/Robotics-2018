/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping.generator;

import Common.mapping.ColorJavaLejos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

public abstract class SingleColorRegion implements ColoredRegion {

    private static final String LOG_TAG = ColoredRegion.class.getSimpleName();

    private final int mColor;

    SingleColorRegion(int color) {
        this.mColor = color;
    }

    @Override
    public Color getDisplayColor(Point point) {
        return ColorJavaLejos.getJavaColor(mColor);
    }

    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        g.setFill(getDisplayColor(null));
    }
}
