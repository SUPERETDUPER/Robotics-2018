package PC;

import Common.Config;
import Common.utils.Logger;
import PC.GUI.GUI;

import java.io.IOException;

class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(String[] args) {
        if (!Config.usePC) {
            Logger.error(LOG_TAG, "Config var 'usePC' isFalse");
        }

        DataReceiver.connect();
        GUI.init();

        try {
            DataReceiver.monitorForData();
        } catch (IOException e) {
            Logger.warning(LOG_TAG, "Lost connection to EV3");
            DataReceiver.close();
            GUI.close();
        }
    }
}