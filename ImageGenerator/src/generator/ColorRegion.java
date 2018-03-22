/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import Common.GUI.Displayable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

abstract class ColorRegion implements Displayable {
    private static final String LOG_TAG = ColorRegion.class.getSimpleName();

    private final Color mColor;

    ColorRegion(Color color) {
        this.mColor = color;
    }

    Color getDisplayColor() {
        return mColor;
    }

    public void displayOnGui(@NotNull GraphicsContext g) {
        g.setFill(getDisplayColor());
    }

    public abstract boolean contains(float x, float y);
}
