/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import PC.GUI.GUILayers.Displayable;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;


/**
Defines a region of the surface that has a color
 */
interface ColoredRegion extends Displayable {
    boolean contains(Point point);
    Color getDisplayColor(Point point);
}