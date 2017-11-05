package geometry;

import lejos.robotics.geometry.Point;

public abstract class ColoredRegion {

    private final int color;

    ColoredRegion(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public abstract boolean contains(Point point);
}
