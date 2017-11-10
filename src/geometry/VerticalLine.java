package geometry;

import lejos.robotics.geometry.Point;

public class VerticalLine extends ColoredRegion {
    private final float y1;
    private final float y2;
    private final float x;
    private final float weight;

    public VerticalLine(int color, float y1, float y2, float x, float weight){
        super(color);

        if (y2 > y1){
            this.y2 = y1;
            this.y1 = y2;
        } else {
            this.y1 = y1;
            this.y2 = y2;
        }

        this.x = x;
        this.weight = weight;
    }

    @Override
    public boolean contains(Point point) {
        return y1 < point.x && point.x < y2 && x - weight < point.x && point.x < x + weight;
    }
}
