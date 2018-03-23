/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.GUI.EventTypes;

import java.io.DataInputStream;
import java.io.IOException;

interface DataChangeListener {
    void dataChanged(EventTypes event, DataInputStream dis) throws IOException;

    void connectionLost();
}
