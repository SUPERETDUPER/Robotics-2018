/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.communication;

import common.TransmittableType;

import java.io.DataInputStream;
import java.io.IOException;

public interface DataReceivedListener {
    /**
     * Called when the data has changed
     *
     * @param event the type of new data
     * @param dis   the data input stream to read from
     * @throws IOException thrown when reading from dataInputStream
     */
    void dataReceived(TransmittableType event, DataInputStream dis) throws IOException;
}
