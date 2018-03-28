/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.EventType;
import common.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Takes data from an input stream, parses it and then notifies the listener
 */
class DataReader {
    private static final String LOG_TAG = DataReader.class.getSimpleName();

    private static DataInputStream dis = null;
    private static DataChangeListener listenerToNotify = null;

    static void init(InputStream inputStream, DataChangeListener listenerToNotify) {
        dis = new DataInputStream(inputStream);
        DataReader.listenerToNotify = listenerToNotify;
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
                EventType dataType = EventType.values()[dis.readByte()];

                if (dataType == EventType.LOG) {
                    System.out.println(dis.readUTF());
                    continue;
                }

                listenerToNotify.dataChanged(dataType, dis);
            }
        } catch (IOException e) {
            listenerToNotify.connectionLost();
        } finally {
            close();
        }
    }

    /**
     * Close the connection
     */
    static synchronized void close() {
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException e) {
                Logger.error(LOG_TAG, "Failed closing socket or dis " + e);
            }
        }
    }
}