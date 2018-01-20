package geometry;

import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import utils.Logger;

import java.awt.*;


/*
Defines a region of the surface of the board
The region has a color
 */
public abstract class ColoredRegion {

    private static final String LOG_TAG = ColoredRegion.class.getSimpleName();

    private final int mColor;

    ColoredRegion(int color) {
        this.mColor = color;
    }

    int getColor() {
        return mColor;
    }

    private java.awt.Color getDisplayColor() {
        switch (mColor) {
            case Color.BLACK:
                return java.awt.Color.BLACK;
            case Color.WHITE:
                return java.awt.Color.WHITE;
            case Color.BLUE:
                return java.awt.Color.BLUE;
            case Color.BROWN:
                return java.awt.Color.DARK_GRAY;
            case Color.GREEN:
                return java.awt.Color.GREEN;
            case Color.RED:
                return java.awt.Color.RED;
            case Color.YELLOW:
                return java.awt.Color.YELLOW;
            default:
                Logger.warning(LOG_TAG, "Region not a valid color");
                return null;
        }
    }

    void setDisplayColor(Graphics g) {
        g.setColor(getDisplayColor());
    }

    abstract boolean contains(Point point);

    abstract void drawRegion(Graphics g);
}
