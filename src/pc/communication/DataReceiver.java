/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.communication;

import common.TransmittableType;
import common.logger.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Takes data from an input stream, parses it and then notifies the listener
 */
public class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();

    private static DataInputStream dis;
    private static DataReceivedListener listenerToNotify;

    private static boolean shouldRun = false;

    public static void init(InputStream inputStream, DataReceivedListener listenerToNotify) {
        dis = new DataInputStream(inputStream);
        DataReceiver.listenerToNotify = listenerToNotify;
        shouldRun = true;
    }

    /**
     * Listen for data
     */
    public static synchronized void read() {
        if (dis == null || listenerToNotify == null) {
            Logger.error(LOG_TAG, "Did not initiate reader");
            return;
        }

        try {
            //noinspection InfiniteLoopStatement
            while (shouldRun) {
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

    public static void stop() {
        shouldRun = false;
    }

    /**
     * Close the connection
     */
    private static void close() {
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException e) {
                Logger.error(LOG_TAG, "Failed closing socket or dis " + e);
            }
        }
    }
}