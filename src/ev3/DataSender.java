/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import common.EventTypes;
import common.LogMessageListener;
import common.Logger;
import common.particles.ParticleAndPoseContainer;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class DataSender {
    private static final String LOG_TAG = DataSender.class.getSimpleName();

    private static DataOutputStream dos;

    public static void connect() {
        Logger.info(LOG_TAG, "Waiting for pc to connect...");

        try {
            Socket socket = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept();
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to connect to pc");
        }

        Logger.info(LOG_TAG, "Connected to Robotics2018.pc");

        //Attach the listener if not using the simulator so that log messages are sent
        if (Config.currentMode == Config.Mode.DUAL) {
            attachLogListener();
        }
    }

    private static void attachLogListener() {
        Logger.setListener(new LogMessageListener() {
            @Override
            public void notifyLogMessage(@NotNull String message) {
                DataSender.sendLogMessage("From EV3 : " + message);
            }
        });
    }

    private static synchronized void sendLogMessage(@NotNull String message) {
        try {
            dos.writeByte(EventTypes.LOG.ordinal());
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            endConnection();
            Logger.error(LOG_TAG, "Failed to send log message");
        }
    }

    public static void sendParticleData(@NotNull ParticleAndPoseContainer data) {
        sendTransmittable(EventTypes.MCL_DATA, data);
    }

    public static void sendCurrentPose(@NotNull Pose currentPose) {
        sendTransmittable(EventTypes.CURRENT_POSE, currentPose);
    }

    public static void sendPath(@NotNull Path path) {
        sendTransmittable(EventTypes.PATH, path);
    }

    private synchronized static void sendTransmittable(@NotNull EventTypes eventType, @NotNull Transmittable transmittable) {
        try {
            dos.writeByte(eventType.ordinal());
            transmittable.dumpObject(dos);
            dos.flush();
        } catch (IOException e) {
            endConnection();
            Logger.error(LOG_TAG, "Failed to send transmittable type : " + eventType.name());
        }
    }

    private static void endConnection() {
        Logger.removeListener();

        try {
            dos.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Could not close data output stream");
        }
    }
}