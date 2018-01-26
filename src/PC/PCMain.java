package PC;

import Common.Config;
import Common.utils.Logger;
import PC.GUI.GUI;

class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();
    private static GUI mGUI;

    public static void main(String[] args) {
        if (!Config.usePC) {
            Logger.error(LOG_TAG, "Config var 'usePC' isFalse");
            return;
        }

        if (DataReceiver.connect()) { //Try to connect
            mGUI = new GUI();
            DataReceiver.monitorForData();
            mGUI.dispose();
        }
    }

    static GUI getGUI() {
        return mGUI;
    }
}