/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.Logger;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private static final String LOG_TAG = Connection.class.getSimpleName();

    private Socket socket;
    private DataInputStream dis;

    boolean connect() {
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

    public DataInputStream getDataInputStream() {
        return dis;
    }

    void close() {
        try {
            socket.close();
            dis.close();
        } catch (IOException e){
            Logger.error(LOG_TAG, "Failed closing socket / dis" + e);
        }
    }
}