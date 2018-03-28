/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.Config;
import common.LogMessageListener;
import common.Logger;
import common.TransmittableType;
import common.particles.ParticleAndPoseContainer;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class PCDataSender {
    private static final String LOG_TAG = PCDataSender.class.getSimpleName();

    @Nullable
    private static DataOutputStream dos;

    public static void init(OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);

        //Attach the listener if not using the simulator so that log messages are sent
        if (Config.currentMode == Config.Mode.DUAL) {
            Logger.setListener(new LogMessageListener() {
                @Override
                public void notifyLogMessage(@NotNull String message) {
                    PCDataSender.sendLogMessage("From EV3 : " + message);
                }
            });
        }
    }

    private static synchronized void sendLogMessage(@NotNull String message) {
        if (dos != null) {
            try {
                dos.writeByte(TransmittableType.LOG.ordinal());
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException e) {
                endConnection();
                Logger.error(LOG_TAG, "Failed to send log message");
            }
        }
    }

    public static void sendParticleData(@NotNull ParticleAndPoseContainer data) {
        sendTransmittable(TransmittableType.MCL_DATA, data);
    }

    public static void sendCurrentPose(@NotNull Pose currentPose) {
        sendTransmittable(TransmittableType.CURRENT_POSE, currentPose);
    }

    public static void sendPath(@NotNull Path path) {
        sendTransmittable(TransmittableType.PATH, path);
    }

    private static synchronized void sendTransmittable(@NotNull TransmittableType eventType, @NotNull Transmittable transmittable) {
        if (dos != null) {
            try {
                dos.writeByte(eventType.ordinal());
                transmittable.dumpObject(dos);
                dos.flush();
            } catch (IOException e) {
                endConnection();
                Logger.error(LOG_TAG, "Failed to send transmittable type : " + eventType.name());
            }
        }
    }

    private static void endConnection() {
        Logger.removeListener();

        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                Logger.error(LOG_TAG, "Could not close data output stream");
            }
        }
    }
}