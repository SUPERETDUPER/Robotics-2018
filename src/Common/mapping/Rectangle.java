/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

class Rectangle extends SingleColorRegion {

    @NotNull
    final lejos.robotics.geometry.Rectangle mRectangle;

    Rectangle(Color color, float x1, float y1, float w, float h) {
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
    }

    public float getWidth(){
        return mRectangle.width;
    }

    public float getHeight(){
        return mRectangle.height;
    }
}