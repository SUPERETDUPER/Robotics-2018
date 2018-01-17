package PC;

import utils.logger.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {
    private static final String LOG_TAG = Connection.class.getSimpleName();
    private static final int EV3_PORT_NUMBER = 1111;

    static class PC {
        private static DataInputStream inputStream;

        public static void connect() {
            try {
                Socket s = new Socket("10.0.1.1", EV3_PORT_NUMBER);
                inputStream = new DataInputStream(s.getInputStream());
            } catch (IOException e) {
                Logger.print(Logger.typeWarning, LOG_TAG, "IOException + " + e);
            }
        }

        public static void read() {

        }

    }

    public static class EV3 {
        public static void connect() {
            try {
                Socket s = new ServerSocket(EV3_PORT_NUMBER).accept();

                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                Logger.print(Logger.typeWarning, LOG_TAG, "IOException + " + e);
            }
        }
    }
}
