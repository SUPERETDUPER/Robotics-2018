/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.communication;

import common.TransmittableType;

import java.io.DataInputStream;
import java.io.IOException;

public interface DataReceivedListener {
    void dataReceived(TransmittableType event, DataInputStream dis) throws IOException;
}
