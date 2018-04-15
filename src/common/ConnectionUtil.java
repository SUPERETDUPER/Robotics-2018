/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

import common.logger.Logger;
import lejos.utility.Delay;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionUtil {
    private static final String LOG_TAG = ConnectionUtil.class.getSimpleName();

    public static Socket createServerSocket(int port) {
        Logger.info(LOG_TAG, "Waiting for PC to connect to EV3...");

        try {
            Socket socket = new ServerSocket(port).accept();
            Logger.info(LOG_TAG, "Connected to PC");
            return socket;
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to PC");
        }
    }

    /**
     * Creates a server socket and gets its output stream
     */
    @NotNull
    public static OutputStream createOutputStream(@NotNull Socket socket) {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException("Could not create output stream from socket");
        }
    }

    @NotNull
    public static Socket createClientSocket(int port, String ipAddress) {
        Logger.info(LOG_TAG, "Attempting to create connection with EV3 ...");

        for (int attempt = 0; attempt < 15; attempt++) {
            try {
                Socket socket = new Socket(ipAddress, port);
                Logger.info(LOG_TAG, "Connected to EV3");
                return socket;
            } catch (IOException e) {
                Delay.msDelay(2000);
            }
        }

        throw new RuntimeException("Failed to get client socket");
    }

    /**
     * @return the input stream of the connection
     */
    @NotNull
    public static InputStream getInputStream(@NotNull Socket socket) {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create input stream from socket");
        }
    }

}
