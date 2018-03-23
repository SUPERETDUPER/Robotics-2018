/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import util.Config;
import util.gui.EventTypes;
import util.Logger;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Manages the connection to the ev3 and calls a listener when new data arrives
 */
class Connection {
    private static final String LOG_TAG = Connection.class.getSimpleName();

    private static Socket socket;
    private static DataInputStream dis;

    private volatile static DataChangeListener listener;

    static void setListener(DataChangeListener listener) {
        Connection.listener = listener;
    }

    /**
     * Connect to ev3
     *
     * @return true if successful
     */
    static boolean connect() {
        Logger.info(LOG_TAG, "Attempting to connect to ev3 ...");
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                socket = new Socket(Config.useSimulator ? "localhost" : Config.EV3_IP_ADDRESS, Config.PORT_TO_CONNECT_ON_EV3);
                dis = new DataInputStream(socket.getInputStream());

                Logger.info(LOG_TAG, "Connected to DataSender");

                return true;

            } catch (IOException e) {
                Delay.msDelay(3000);
            }
        }

        return false;
    }

    /**
     * Listen for data
     */
    static void listen() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                readNext();
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.connectionLost();
            }
        } finally {
            close();
        }
    }

    /**
     * Close the connection
     */
    static void close() {
        try {
            socket.close();
            dis.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed closing socket or dis" + e);
        }
    }

    private synchronized static void readNext() throws IOException {
        if (listener != null) {
            EventTypes dataType = EventTypes.values()[dis.readByte()];

            listener.dataChanged(dataType, dis);
        }
    }
}