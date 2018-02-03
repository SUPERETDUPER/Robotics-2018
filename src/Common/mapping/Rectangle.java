package Common.mapping;

import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

//TODO Fix class (instance variables)
class Rectangle extends SingleColorRegion {

    @NotNull
    final lejos.robotics.geometry.Rectangle mRectangle;

    Rectangle(int color, float x1, float y1, float w, float h) {
        super(color);
        mRectangle = new lejos.robotics.geometry.Rectangle(x1, y1, w, h);
    }

    @Override
    public void displayOnGui(@NotNull Graphics g) {
        super.displayOnGui(g);
        g.fillRect((int) mRectangle.x, (int) mRectangle.y, (int) mRectangle.width, (int) mRectangle.height);
    }

    @Override
    public boolean contains(@NotNull Point point) {
        return mRectangle.contains(point);
        //return x1 < point.getX() && point.getX() < x1 + w && y1 < point.getY() && point.getY() < y1 + h;
    }

    public float getX1() {
        return mRectangle.x;
    }

    public float getY1() {
        return mRectangle.y;
    }

    public float getHeight() {
        return mRectangle.height;
    }

    public float getWidth() {
        return mRectangle.width;
    }
}
