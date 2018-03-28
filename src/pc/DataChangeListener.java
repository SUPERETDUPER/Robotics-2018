/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.EventType;

import java.io.DataInputStream;
import java.io.IOException;

interface DataChangeListener {
    void dataChanged(EventType event, DataInputStream dis) throws IOException;

    void connectionLost();
}
