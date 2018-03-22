/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

class Rectangle extends ColorRegion {

    @NotNull
    private final lejos.robotics.geometry.Rectangle mRectangle;

    Rectangle(Color color, float x1, float y1, float w, float h) {
        super(color);
        mRectangle = new lejos.robotics.geometry.Rectangle(x1, y1, w, h);
    }

    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        super.displayOnGui(g);
        g.fillRect((int) mRectangle.x, (int) mRectangle.y, (int) mRectangle.width, (int) mRectangle.height);
    }

    @Override
    public boolean contains(float x, float y) {
        return mRectangle.contains(x,y);
    }

    public float getWidth(){
        return mRectangle.width;
    }

    public float getHeight(){
        return mRectangle.height;
    }
}