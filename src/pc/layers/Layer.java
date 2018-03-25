/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.layers;

import common.Config;
import common.mapping.SurfaceMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * A canvas that is displayed on the gui.
 */
public abstract class Layer extends Canvas {

    private final GraphicsContext graphics = getGraphicsContext2D();

    Layer() {
        super(SurfaceMap.getWidth(), SurfaceMap.getHeight());

        applyTransformations();
        draw();
    }

    private void applyTransformations() {
        if (shouldInvert()) {
            graphics.scale(Config.GUI_DISPLAY_RATIO, -Config.GUI_DISPLAY_RATIO);
            graphics.translate(0, -SurfaceMap.getHeight());
        } else {
            graphics.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);
        }
    }

    synchronized void draw() {
        graphics.clearRect(0, 0, getWidth(), getHeight()); //Clear layer

        displayOnGui(graphics); //Display on gui
    }


    /**
     * Called by JavaFX to request the Object to be draw on the Graphics
     *
     * @param g Graphics object should be drawn on
     */
    abstract void displayOnGui(GraphicsContext g);

    /**
     * JavaFx has an inverted display ( (0,0) is the top left). However most of the data uses (0,0) in bottom left.
     * If shouldInvert true JavaFX will flip to allow (0,0) to be in the bottom left when being displayed
     *
     * @return true if the object should be displayed with (0,0) in bottom left (traditional cartesian)
     */
    abstract boolean shouldInvert();
}
