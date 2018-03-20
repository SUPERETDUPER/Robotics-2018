/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import PC.Displayable;
import lejos.robotics.geometry.Point;


/**
Defines a region of the surface that has a color
 */
interface ColoredRegion extends Displayable {
    boolean contains(Point point);
    java.awt.Color getDisplayColor(Point point);
}