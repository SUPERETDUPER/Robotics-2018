/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageGenerator;

import javafx.scene.paint.Color;

abstract class ColorRegion {
    private static final String LOG_TAG = ColorRegion.class.getSimpleName();

    private final Color mColor;

    ColorRegion(Color color) {
        this.mColor = color;
    }

    Color getDisplayColor() {
        return mColor;
    }

    public abstract boolean contains(float x, float y);
}
