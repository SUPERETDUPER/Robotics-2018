/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package util.gui;

import javafx.scene.canvas.GraphicsContext;

/**
 * Any object that can be draw onto the util.gui should implement
 */
public interface Displayable {

    /**
     * Called by JavaFX to request the Object to be draw on the Graphics
     *
     * @param g Graphics object should be drawn on
     */
    void displayOnGui(GraphicsContext g);

    /**
     * JavaFx has an inverted display ( (0,0) is the top left). However most of the data uses (0,0) in bottom left.
     * If invert true JavaFX will flip to allow (0,0) to be in the bottom left when being displayed
     *
     * @return true if the object should be displayed with (0,0) in bottom left (traditional cartesian)
     */
    boolean invert();
}
