/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC.GUI.GUILayers;

import javafx.scene.canvas.GraphicsContext;

/*
Any object that can be draw onto the GUI should implement
 */
public interface Displayable {
    void displayOnGui(GraphicsContext g);
}
