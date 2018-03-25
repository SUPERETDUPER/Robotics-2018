/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Config;
import common.EventTypes;
import common.Logger;
import lejos.utility.Delay;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Connect to ev3
     *
     * @return true if successful
     */
    static boolean connect() {
        Logger.info(LOG_TAG, "Attempting to connect to EV3 ...");
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                socket = new Socket(getIPAddress(), Config.PORT_TO_CONNECT_ON_EV3);
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
    static void listen(@NotNull DataChangeListener listener) {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                readNext(listener);
            }
        } catch (IOException e) {
            listener.connectionLost();
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
            Logger.error(LOG_TAG, "Failed closing socket or dis " + e);
        }
    }

    private synchronized static void readNext(@NotNull DataChangeListener listener) throws IOException {
        EventTypes dataType = EventTypes.values()[dis.readByte()];

        if (dataType == EventTypes.LOG) {
            System.out.println(dis.readUTF());
        } else {
            listener.dataChanged(dataType, dis);
        }

    }

    @NotNull
    private static String getIPAddress() {
        return Config.currentMode == Config.Mode.SIM ? "localhost" : Config.EV3_IP_ADDRESS;
    }
}