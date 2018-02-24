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

package PC;

import Common.Config;
import Common.EventTypes;
import Common.Logger;
import PC.GUI.GUI;
import lejos.utility.Delay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();
    private static DataInputStream dis;
    private static Socket socket;

    static void connect() {
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                socket = new Socket(getIpAddress(), Config.PORT_TO_CONNECT_ON_EV3);
                dis = new DataInputStream(socket.getInputStream());

                Logger.info(LOG_TAG, "Connected to DataSender");

                return;

            } catch (IOException e) {
                Logger.warning(LOG_TAG, "Failed attempt " + attempt + " to connect to EV3");
                Delay.msDelay(3000);
            }
        }

        Logger.error(LOG_TAG, "Failed to connect to EV3");
    }

    static void monitorForData() throws IOException {
        //noinspection InfiniteLoopStatement
        for (; ; Thread.yield()) {
            DataReceiver.read();
        }
    }


    private synchronized static void read() throws IOException {
        EventTypes dataType = EventTypes.values()[dis.readByte()];

//        Logger.debug(LOG_TAG, "Received Event " + dataType.name());

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
                Logger.error(LOG_TAG, "Not a recognized event type");
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

    @NotNull
    @Contract(pure = true)
    private static String getIpAddress() {
        return Config.useSimulator ? "localhost" : Config.EV3_IP_ADDRESS;
    }
}