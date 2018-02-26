/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC.GUI;

import java.awt.*;

/*
Any object that can be draw onto the GUI should implement
 */
public interface Displayable {
    void displayOnGui(Graphics g);
}
