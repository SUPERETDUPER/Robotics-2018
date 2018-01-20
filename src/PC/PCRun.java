package PC;

import utils.Config;
import utils.Logger;

class PCRun {

    private static final String LOG_TAG = PCRun.class.getSimpleName();

    public static void main(String[] args) {
        Connection.runningOnEV3 = false;

        if (!Config.USING_PC) {
            Logger.error(LOG_TAG, "Config var 'USING_PC' is false");
            return;
        }


        if (Connection.PC.connect()) { //Try to connect

            GUI.init(); //Create GUI

            monitorForData();

            GUI.get().close();  //Close GUI
        }
    }

    private static void monitorForData() {
        while (Connection.PC.read()) { //Constantly check for new data
            Thread.yield();
        }

        Logger.error(LOG_TAG, "Lost connection to EV3");
    }
}