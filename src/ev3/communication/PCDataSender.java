/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.TransmittableType;
import common.logger.Logger;
import lejos.robotics.Transmittable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class PCDataSender implements DataSender {
    private static final String LOG_TAG = PCDataSender.class.getSimpleName();

    @NotNull
    private final DataOutputStream dos;

    @Nullable
    private final LostConnectionListener lostConnectionListener;

    public PCDataSender(@NotNull OutputStream outputStream, @Nullable LostConnectionListener lostConnectionListener) {
        dos = new DataOutputStream(outputStream);
        this.lostConnectionListener = lostConnectionListener;
    }

    public synchronized void sendTransmittable(@NotNull TransmittableType eventType, @NotNull Transmittable transmittable) {
        try {
            dos.writeByte(eventType.ordinal());
            transmittable.dumpObject(dos);
            dos.flush();
        } catch (IOException e) {
            if (lostConnectionListener != null) {
                lostConnectionListener.lostConnection();
            }

            Logger.error(LOG_TAG, "Lost connection");
        }
    }

    @Override
    public void close() {
        try {
            dos.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Could not close data output stream");
        }
    }
}