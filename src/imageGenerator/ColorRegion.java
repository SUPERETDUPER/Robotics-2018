/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageGenerator;

import java.awt.*;

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
