package Common.mapping;

import PC.GUI.Displayable;
import lejos.robotics.geometry.Point;


/*
Defines a region of the surface of the board
The region has a color
 */
interface ColoredRegion extends Displayable {
    int getColorAtPoint(Point point);

    boolean contains(Point point);
}