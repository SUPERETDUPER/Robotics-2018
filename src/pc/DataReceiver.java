/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.logger.LogMessage;
import common.logger.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Takes data from an input stream, parses it and then notifies the listener
 */
class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();

    private final DataInputStream dis;

    DataReceiver(InputStream inputStream) {
        this.dis = new DataInputStream(inputStream);
    }

    /**
     * Listen for data
     */
    synchronized void read() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                LogMessage logMessage = new LogMessage();
                logMessage.loadObject(dis);
                logMessage.printToSysOut("From EV3 : ");
            }
        } catch (IOException e) {
            Logger.warning(LOG_TAG, "Connection lost. Could not read input stream");
        } finally {
            close();
        }
    }

    /**
     * Close the connection
     */
    private void close() {
        try {
            dis.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed closing socket or dis " + e);
        }
    }
}