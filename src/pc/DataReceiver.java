/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.logger.Logger;
import common.TransmittableType;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Takes data from an input stream, parses it and then notifies the listener
 */
class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();

    private static DataInputStream dis = null;
    private static DataReceivedListener listenerToNotify = null;

    static void init(InputStream inputStream, DataReceivedListener listenerToNotify) {
        dis = new DataInputStream(inputStream);
        DataReceiver.listenerToNotify = listenerToNotify;
    }

    /**
     * Listen for data
     */
    static synchronized void read() {
        if (dis == null || listenerToNotify == null) {
            Logger.error(LOG_TAG, "Did not initiate reader");
            return;
        }

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                TransmittableType dataType = TransmittableType.values()[dis.readByte()];

                if (dataType == TransmittableType.LOG) {
                    System.out.println(dis.readUTF());
                    continue;
                }

                listenerToNotify.dataReceived(dataType, dis);
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
    static void close() {
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException e) {
                Logger.error(LOG_TAG, "Failed closing socket or dis " + e);
            }
        }
    }
}