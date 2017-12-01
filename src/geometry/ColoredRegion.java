package geometry;

import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import utils.logger.Logger;

import java.awt.*;



public abstract class ColoredRegion {

    private static final String LOG_TAG = ColoredRegion.class.getSimpleName();

    private final int color;

    ColoredRegion(int color) {
        this.color = color;
    }

    int getColor() {
        return color;
    }

    public abstract boolean contains(Point point);

    abstract void drawRegion(Graphics g);

    java.awt.Color getAwtColor(){
        switch (color){
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
                Logger.log(Logger.typeWarning, LOG_TAG, "No color defined");
                return null;
        }
    }
}
