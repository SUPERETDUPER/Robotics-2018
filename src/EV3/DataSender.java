/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package EV3;

import Common.Config;
import Common.EventTypes;
import Common.Particles.ParticleData;
import Common.Logger;
import lejos.robotics.Transmittable;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataSender {
    private static final String LOG_TAG = DataSender.class.getSimpleName();

    private static DataOutputStream dos;
    private static boolean isConnected = false;

    static void connect() {
        if (isConnected) {
            Logger.warning(LOG_TAG, "Already connected to Robotics2018.PC");
        }

        Logger.info(LOG_TAG, "Waiting for PC to connect...");

        try {
            Socket socket = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept();
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to connect to PC");
        }

        isConnected = true;
        Logger.info(LOG_TAG, "Connected to Robotics2018.PC");
    }

    public synchronized static void sendLogMessage(@NotNull String message) {
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

    static void sendPath(@NotNull Path path) {
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
//            Logger.debug(LOG_TAG, "Sent : " + eventType.name());
        } catch (IOException e) {
            isConnected = false;
            Logger.error(LOG_TAG, "Failed to send transmittable type : " + eventType.name());
        }
    }
}