package PC;

import com.sun.istack.internal.NotNull;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnector;
import lejos.utility.Delay;
import navigation.MyPoseProvider;
import utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Connection {

    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static boolean runningOnEV3;
    private static boolean connected = false;

    public enum EventTypes {
        MCL_DATA,
        LOG
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

                NXTConnection socketConnection = new SocketConnector().connect(EV3_IP_ADDRESS, 2);

                if (socketConnection != null) {
                    dis = socketConnection.openDataInputStream();
                    connected = true;
                    Logger.info(LOG_TAG, "Connected to EV3");
                    return true;
                }

                Logger.warning(LOG_TAG, "Failed to connect to EV3; attempt " + attempts);

                Delay.msDelay(3000L);
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
                        GUI.get().repaint();
                        break;
                    case LOG:
                        System.out.println(dis.readUTF());
                        break;
                    default:
                        Logger.warning(LOG_TAG, "Not a recognized event type");
                }

                return true;
            } catch (IOException e) {
                connected = false;
                Logger.error(LOG_TAG, "Could not read data input stream" + e);
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
            }
        }
    }
}