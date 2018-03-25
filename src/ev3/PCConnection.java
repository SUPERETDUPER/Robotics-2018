/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import common.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

public class PCConnection {
    private static final String LOG_TAG = PCConnection.class.getSimpleName();

    @Nullable
    public static OutputStream getConnection() {
        Logger.info(LOG_TAG, "Waiting for pc to getConnection...");

        OutputStream outputStream;

        try {
            outputStream = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept().getOutputStream();
        } catch (IOException e) {
            return null;
        }

        Logger.info(LOG_TAG, "Connected to Robotics2018.pc");

        return outputStream;
    }
}
