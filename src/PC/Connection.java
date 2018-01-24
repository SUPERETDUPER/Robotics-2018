package PC;

import com.sun.istack.internal.NotNull;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnector;
import lejos.utility.Delay;
import navigation.MyPoseProvider;
import utils.Config;
import utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Connection {

    public static RUNNING_ON runningOn;

    public static boolean isConnected() {
        return connected;
    }

    private static boolean connected = false;

    public enum EventTypes {
        MCL_DATA,
        LOG
    }

    public enum RUNNING_ON {
        PC,
        EV3,
        EV3_SIM
    }

    static class PC {
        private static final String LOG_TAG = Connection.PC.class.getSimpleName();

        private static DataInputStream dis;

        @NotNull
        public static boolean connect() {
            if (connected) {
                Logger.warning(LOG_TAG, "Already connected");
                return true;
            }


            for (int attempts = 1; attempts < 6; attempts++) {

                NXTConnection socketConnection;

                if (Config.currentMode == Config.Mode.SIM) {
                    socketConnection = new SocketConnector().connect("localhost", 2);
                } else if (Config.currentMode == Config.Mode.DUAL) {
                    socketConnection = new SocketConnector().connect(Config.EV3_IP_ADDRESS, 2);
                } else {
                    Logger.error(LOG_TAG, "Current mode is not SIM or DUAL should not be connecting");
                    return false;
                }


                if (socketConnection != null) {
                    dis = socketConnection.openDataInputStream();
                    connected = true;
                    Logger.info(LOG_TAG, "Connected to EV3");
                    return true;
                }

                Logger.warning(LOG_TAG, "Failed to connect to EV3; attempt " + attempts);

                Delay.msDelay(3000);
            }

            Logger.error(LOG_TAG, "Could not connect to EV3");
            return false;
        }

        @NotNull
        public static boolean read() {
            try {
                EventTypes dataType = EventTypes.values()[dis.readByte()];

                Logger.debug(LOG_TAG, "Received Event " + dataType.name());

                switch (dataType) {
                    case MCL_DATA:
                        MyPoseProvider.get().loadObject(dis);
                        GUI.update();
                        break;
                    case LOG:
                        System.out.println(dis.readUTF());
                        break;
                    default:
                        Logger.error(LOG_TAG, "Not a recognized event type");
                }

                return true;
            } catch (IOException e) {
                connected = false;
                Logger.error(LOG_TAG, "Could not read data input stream " + e);
                return false;
            }
        }
    }

    public static class EV3 {
        private static final String LOG_TAG = Connection.EV3.class.getSimpleName();

        private static DataOutputStream dos;

        @NotNull
        public static boolean connect() {
            if (connected) {
                Logger.warning(LOG_TAG, "Already connected to PC");
                return true;
            }

            Logger.info(LOG_TAG, "Waiting for connection...");

            NXTConnection socketConnection = new SocketConnector().waitForConnection(0, 2);  // Arguments are not used, see source

            if (socketConnection == null) {
                Logger.error(LOG_TAG, "Could not connect to PC");
                return false;
            }

            dos = socketConnection.openDataOutputStream();
            connected = true;
            Logger.info(LOG_TAG, "Connected to PC");
            return true;
        }

        public static void sendMCLData() {
            try {
                Logger.debug(LOG_TAG, "Sending MCL_DATA...");
                dos.writeByte(EventTypes.MCL_DATA.ordinal());
                MyPoseProvider.get().dumpObject(dos);
                dos.flush();
                Logger.debug(LOG_TAG, "Sent");
            } catch (IOException e) {
                connected = false;
                Logger.error(LOG_TAG, "Failed to send MCLData");
            }
        }

        public static void sendLogMessage(String message) {
            if (connected) {
                try {
                    dos.writeByte(EventTypes.LOG.ordinal());
                    dos.writeUTF(message);
                } catch (IOException e) {
                    connected = false;
                    Logger.error(LOG_TAG, "Failed to send log message");
                }
            } else {
                Logger.warning(LOG_TAG, "Not connected cannot send log message");
            }
        }
    }
}