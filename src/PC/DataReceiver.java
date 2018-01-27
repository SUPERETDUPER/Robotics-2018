package PC;

import Common.Config;
import Common.EventTypes;
import Common.utils.Logger;
import PC.GUI.GUI;
import com.sun.istack.internal.NotNull;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnector;
import lejos.robotics.pathfinding.Path;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.IOException;

public class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();
    private static boolean isConnected = false;
    private static DataInputStream dis;

    @NotNull
    public static boolean connect() {
        if (!Config.usePC) {
            Logger.error(LOG_TAG, "'usePC' is set to false");
        }

        if (isConnected) {
            Logger.warning(LOG_TAG, "Already isConnected");
            return true;
        }


        for (int attempts = 1; attempts < 6; attempts++) {

            NXTConnection socketConnection;

            if (Config.useSimulator) {
                socketConnection = new SocketConnector().connect("localhost", 2);
            } else {
                socketConnection = new SocketConnector().connect(Config.EV3_IP_ADDRESS, 2);
            }


            if (socketConnection != null) {
                dis = socketConnection.openDataInputStream();
                isConnected = true;
                Logger.info(LOG_TAG, "Connected to DataSender");
                return true;
            }

            Logger.warning(LOG_TAG, "Failed to connect to DataSender; attempt " + attempts);

            Delay.msDelay(3000);
        }

        Logger.error(LOG_TAG, "Could not connect to DataSender");
        return false;
    }

    static void monitorForData() {
        while (read()) { //Constantly check for new data
            Thread.yield();
        }

        Logger.error(LOG_TAG, "Lost connection to DataSender");
    }

    @NotNull
    private static boolean read() {
        try {
            EventTypes dataType = EventTypes.values()[dis.readByte()];

            Logger.debug(LOG_TAG, "Received Event " + dataType.name());

            switch (dataType) {
                case MCL_DATA:
                    PCMain.getGUI().updateMCLData(dis);
                    PCMain.getGUI().repaint();
                    break;
                case LOG:
                    System.out.println(dis.readUTF());
                    break;
                case PATH:
                    Path path = new Path();
                    path.loadObject(dis);
                    GUI.path = path;
                    PCMain.getGUI().repaint();
                    break;
                default:
                    Logger.error(LOG_TAG, "Not a recognized event type");
            }

            return true;
        } catch (IOException e) {
            isConnected = false;
            return false;
        }
    }
}

