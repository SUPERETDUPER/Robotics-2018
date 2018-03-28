/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.TransmittableType;

import java.io.DataInputStream;
import java.io.IOException;

interface DataChangeListener {
    void dataChanged(TransmittableType event, DataInputStream dis) throws IOException;

    void connectionLost();
}
