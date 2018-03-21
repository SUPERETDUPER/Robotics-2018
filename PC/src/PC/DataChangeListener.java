/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import GUI.EventTypes;

import java.io.DataInputStream;
import java.io.IOException;

public interface DataChangeListener {
    void dataChanged(EventTypes event, DataInputStream dis) throws IOException;

    void connectionLost();
}
