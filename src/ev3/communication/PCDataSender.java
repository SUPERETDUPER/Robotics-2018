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

import javax.xml.crypto.Data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class PCDataSender implements DataSender {
    private static final String LOG_TAG = PCDataSender.class.getSimpleName();

    @Nullable
    private DataOutputStream dos;

    public PCDataSender(OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    public synchronized void sendLogMessage(@NotNull String message) {
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

    public void sendParticleData(@NotNull ParticleAndPoseContainer data) {
        sendTransmittable(TransmittableType.MCL_DATA, data);
    }

    public void sendCurrentPose(@NotNull Pose currentPose) {
        sendTransmittable(TransmittableType.CURRENT_POSE, currentPose);
    }

    public void sendPath(@NotNull Path path) {
        sendTransmittable(TransmittableType.PATH, path);
    }

    public synchronized void sendTransmittable(@NotNull TransmittableType eventType, @NotNull Transmittable transmittable) {
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

    private void endConnection() {
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