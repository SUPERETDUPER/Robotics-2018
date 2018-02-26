/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import PC.GUI.Displayable;
import lejos.robotics.geometry.Point;


/**
Defines a region of the surface that has a color
 */
interface ColoredRegion extends Displayable {
    int getColorAtPoint(Point point);
    boolean contains(Point point);
}