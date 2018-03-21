/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import Common.mapping.ColorJavaLejos;
import Common.GUI.Displayable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

abstract class ColorRegion implements Displayable {
    private static final String LOG_TAG = ColorRegion.class.getSimpleName();

    private final int mColor;

    ColorRegion(int color) {
        this.mColor = color;
    }

    public Color getDisplayColor() {
        return ColorJavaLejos.getJavaColor(mColor);
    }

    public void displayOnGui(@NotNull GraphicsContext g) {
        g.setFill(getDisplayColor());
    }

    public abstract boolean contains(Point point);
}
