/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import common.Config;
import common.mapping.SurfaceMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * A layer that is displayed on the gui. It has a drawn variable that is true when its just been drawn and set false when the value is changed
 */
public abstract class Layer extends Canvas {
    Layer() {
        super(SurfaceMap.getWidth(), SurfaceMap.getHeight());

        if (invert()) {
            getGraphicsContext2D().scale(Config.GUI_DISPLAY_RATIO, -Config.GUI_DISPLAY_RATIO);
            getGraphicsContext2D().translate(0, -getHeight());
        } else {
            getGraphicsContext2D().scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);
        }
    }

    public synchronized void draw() {
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

        displayOnGui(getGraphicsContext2D());
    }


    /**
     * Called by JavaFX to request the Object to be draw on the Graphics
     *
     * @param g Graphics object should be drawn on
     */
    abstract void displayOnGui(GraphicsContext g);

    /**
     * JavaFx has an inverted display ( (0,0) is the top left). However most of the data uses (0,0) in bottom left.
     * If invert true JavaFX will flip to allow (0,0) to be in the bottom left when being displayed
     *
     * @return true if the object should be displayed with (0,0) in bottom left (traditional cartesian)
     */
    abstract boolean invert();
}
