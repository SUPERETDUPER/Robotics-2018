package Common.mapping;

import Common.utils.Logger;
import PC.GUI.Displayable;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;

import java.awt.*;


/*
Defines a region of the surface of the board
The region has a color
 */
interface ColoredRegion extends Displayable {
    int getColorAtPoint(Point point);

    boolean contains(Point point);

    void displayOnGui(Graphics g);
}