/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.EventTypes;
import Common.Logger;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private static final String LOG_TAG = Connection.class.getSimpleName();

    private static Socket socket;
    private static DataInputStream dis;

    private static DataChangeListener listener;

    public static void setListener(DataChangeListener listener) {
        Connection.listener = listener;
    }

    static boolean connect() {
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                socket = new Socket(Config.useSimulator ? "localhost" : Config.EV3_IP_ADDRESS, Config.PORT_TO_CONNECT_ON_EV3);
                dis = new DataInputStream(socket.getInputStream());

                Logger.info(LOG_TAG, "Connected to DataSender");

                return true;

            } catch (IOException e) {
                Logger.warning(LOG_TAG, "Failed attempt " + attempt + " to connect to EV3");
                Delay.msDelay(3000);
            }
        }

        return false;
    }

    static void listen() {
        try {
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

    private static void readNext() throws IOException {
        EventTypes dataType = EventTypes.values()[dis.readByte()];

        if (listener != null) {
            listener.dataChanged(dataType, dis);
        }
    }

    private static void close() {
        try {
            socket.close();
            dis.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed closing socket / dis" + e);
        }
    }
}