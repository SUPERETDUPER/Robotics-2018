/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageGenerator;

import java.awt.*;

/**
 * Defines what a region should do (aka. know if a point is contained, have a color)
 */
abstract class ColorRegion {
    private final Color mColor;

    ColorRegion(Color color) {
        this.mColor = color;
    }

    Color getDisplayColor() {
        return mColor;
    }

    public abstract boolean contains(float x, float y);
}
