package EV3;

import Common.EventTypes;
import Common.navigation.MCL.MCLData;
import Common.utils.Logger;
import com.sun.istack.internal.NotNull;
import lejos.robotics.Transmittable;
import lejos.robotics.pathfinding.Path;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataSender {
    private static final String LOG_TAG = DataSender.class.getSimpleName();

    private static DataOutputStream dos;
    private static boolean isConnected = false;

    public static boolean isConnected() {
        return isConnected;
    }

    @NotNull
    public static void connect() {
        if (isConnected) {
            Logger.warning(LOG_TAG, "Already connected to Robotics2018.PC");
        }

        Logger.info(LOG_TAG, "Waiting for connection...");

        try {
            Socket socket = new ServerSocket(8888).accept();
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to connect to PC");
        }

        isConnected = true;
        Logger.info(LOG_TAG, "Connected to Robotics2018.PC");
    }

    public static void sendMCLData(MCLData data) {
        if (isConnected) {
            sendTransmittable(EventTypes.MCL_DATA, data);
        } else {
            Logger.warning(LOG_TAG, "Not connected; could not send MCLData");
        }
    }

    public static void sendPath(Path path) {
        if (isConnected) {
            sendTransmittable(EventTypes.PATH, path);
        } else {
            Logger.warning(LOG_TAG, "Not connected; could not sendPath");
        }
    }

    public synchronized static void sendLogMessage(String message) {
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
            Logger.warning(LOG_TAG, "Not connected cannot send log message");
        }
    }

    private synchronized static void sendTransmittable(EventTypes eventType, Transmittable transmittable) {
        try {
            dos.writeByte(eventType.ordinal());
            transmittable.dumpObject(dos);
            dos.flush();
            Logger.info(LOG_TAG, "Sent : " + eventType.name());
        } catch (IOException e) {
            isConnected = false;
            Logger.error(LOG_TAG, "Failed to send transmittable type : " + eventType.name());
        }
    }
}