package EV3;

import Common.EventTypes;
import Common.navigation.MCL.MCLData;
import Common.utils.Logger;
import com.sun.istack.internal.NotNull;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnector;
import lejos.robotics.pathfinding.Path;

import java.io.DataOutputStream;
import java.io.IOException;

public class DataSender {
    private static final String LOG_TAG = DataSender.class.getSimpleName();

    private static DataOutputStream dos;
    private static boolean isConnected = false;

    public static boolean isConnected() {
        return isConnected;
    }

    @NotNull
    public static boolean connect() {
        if (isConnected) {
            Logger.warning(LOG_TAG, "Already connected to Robotics2018.PC");
            return true;
        }

        Logger.info(LOG_TAG, "Waiting for connection...");

        NXTConnection socketConnection = new SocketConnector().waitForConnection(0, 2);  // Arguments are not used, see source

        if (socketConnection == null) {
            Logger.error(LOG_TAG, "Could not connect to Robotics2018.PC");
            return false;
        }

        dos = socketConnection.openDataOutputStream();
        isConnected = true;
        Logger.info(LOG_TAG, "Connected to Robotics2018.PC");
        return true;
    }

    public static void sendMCLData(MCLData data) {
        if (isConnected) {
            try {
                dos.writeByte(EventTypes.MCL_DATA.ordinal());
                data.dumpObject(dos);
                dos.flush();
                Logger.debug(LOG_TAG, "Sent MCLData");
            } catch (IOException e) {
                isConnected = false;
                Logger.error(LOG_TAG, "Failed to send MCLData");
            }
        } else {
            Logger.warning(LOG_TAG, "Not connected; could not send MCLData");
        }
    }

    public static void sendPath(Path path) {
        if (isConnected) {
            try {
                dos.writeByte(EventTypes.PATH.ordinal());
                path.dumpObject(dos);
            } catch (IOException e) {
                isConnected = false;
                Logger.error(LOG_TAG, "Failed to send move " + e);
            }
        } else {
            Logger.warning(LOG_TAG, "Not connected; could not sendPath");
        }
    }

    public static void sendLogMessage(String message) {
        if (isConnected) {
            try {
                dos.writeByte(EventTypes.LOG.ordinal());
                dos.writeUTF(message);
            } catch (IOException e) {
                isConnected = false;
                Logger.error(LOG_TAG, "Failed to send log message");
            }
        } else {
            Logger.warning(LOG_TAG, "Not connected cannot send log message");
        }
    }
}
