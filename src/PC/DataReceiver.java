package PC;

import Common.Config;
import Common.EventTypes;
import Common.utils.Logger;
import PC.GUI.GUI;
import com.sun.istack.internal.NotNull;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();
    private static DataInputStream dis;
    private static Socket socket;

    @NotNull
    public static void connect() {
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                socket = new Socket(Config.useSimulator ? "localhost" : Config.EV3_IP_ADDRESS, 8888);
                dis = new DataInputStream(socket.getInputStream());

                Logger.info(LOG_TAG, "Connected to DataSender");

                return;

            } catch (IOException e) {
                Logger.warning(LOG_TAG, "Failled attempt " + attempt + " to connect to EV3");
                Delay.msDelay(3000);
            }
        }

        Logger.error(LOG_TAG, "Failed to connect to EV3");
    }

    static void monitorForData() throws IOException {
        for (; ; Thread.yield()) {
            DataReceiver.read();
        }
    }

    @NotNull
    private synchronized static void read() throws IOException {
        EventTypes dataType = EventTypes.values()[dis.readByte()];

        Logger.debug(LOG_TAG, "Received Event " + dataType.name());

        switch (dataType) {
            case MCL_DATA:
                GUI.updateMCLData(dis);
                break;
            case LOG:
                System.out.println(dis.readUTF());
                break;
            case PATH:
                GUI.updatePaths(dis);
                break;
            default:
                Logger.warning(LOG_TAG, "Not a recognized event type");
        }
    }

    static void close() {
        try {
            dis.close();
            socket.close();
        } catch (IOException e) {
            Logger.warning(LOG_TAG, "Unable to close data input stream or socket");
        }
    }
}