/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.TransmittableType;

import java.io.DataInputStream;
import java.io.IOException;

interface DataReceivedListener {
    void dataReceived(TransmittableType event, DataInputStream dis) throws IOException;
}
