/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.mapping.SurfaceMap;
import Common.GUI.Displayable;
import javafx.scene.canvas.Canvas;

/**
 * A layer that is displayed on the GUI. It has a drawn variable that is true when its just been drawn and set false when the value is changed
 */
class Layer extends Canvas {
    private boolean isDrawn = false;

    private final Displayable displayable;

    Layer(Displayable displayable) {
        super(SurfaceMap.getWidth(), SurfaceMap.getHeight());

        this.displayable = displayable;

        getGraphicsContext2D().scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

    }

    Displayable getDisplayable() {
        return displayable;
    }

    synchronized void draw() {

        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

        displayable.displayOnGui(getGraphicsContext2D());
        isDrawn = true;
    }

    void markNotDrawn() {
        isDrawn = false;
    }

    boolean isDrawn() {
        return isDrawn;
    }
}
