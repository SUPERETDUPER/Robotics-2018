/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import util.Config;
import util.gui.EventTypes;
import util.gui.ParticleData;
import util.Logger;
import util.NewEV3LogMessageListener;
import lejos.robotics.Transmittable;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class DataSender {
    private static final String LOG_TAG = DataSender.class.getSimpleName();

    private static DataOutputStream dos;
    private static boolean isConnected = false;

    static {
        Logger.setListener(new NewEV3LogMessageListener() {
            @Override
            public void notifyNewEV3Message(@NotNull String message) {
                DataSender.notifyNewEV3Message(message);
            }
        }); // To attach to logger
    }

    static void connect() {
        if (isConnected) {
            Logger.warning(LOG_TAG, "Already connected to Robotics2018.pc");
        }

        Logger.info(LOG_TAG, "Waiting for pc to connect...");

        try {
            Socket socket = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept();
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to connect to pc");
        }

        isConnected = true;
        Logger.info(LOG_TAG, "Connected to Robotics2018.pc");
    }

    private static synchronized void notifyNewEV3Message(@NotNull String message) {
        if (isConnected) {
            try {
                dos.writeByte(EventTypes.LOG.ordinal());
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException e) {
                isConnected = false;
                Logger.error(LOG_TAG, "Failed to send log message");
            }
        } else {
            System.out.println(message);
        }
    }

    public static void sendParticleData(@NotNull ParticleData data) {
        if (isConnected) {
            sendTransmittable(EventTypes.MCL_DATA, data);
        } else {
            Logger.warning(LOG_TAG, "Not connected; could not send ParticleData");
        }
    }

    public static void sendPath(@NotNull Path path) {
        if (isConnected) {
            sendTransmittable(EventTypes.PATH, path);
        } else {
            Logger.warning(LOG_TAG, "Not connected; could not sendPath");
        }
    }

    private synchronized static void sendTransmittable(@NotNull EventTypes eventType, @NotNull Transmittable transmittable) {
        try {
            dos.writeByte(eventType.ordinal());
            transmittable.dumpObject(dos);
            dos.flush();
        } catch (IOException e) {
            isConnected = false;
            Logger.error(LOG_TAG, "Failed to send transmittable type : " + eventType.name());
        }
    }
}