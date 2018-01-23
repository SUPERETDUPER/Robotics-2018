package PC;

import utils.Config;
import utils.Logger;

class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(String[] args) {
        Connection.runningOn = Connection.RUNNING_ON.PC;

        if (Config.currentMode != Config.Mode.SIM && Config.currentMode != Config.Mode.DUAL) {
            Logger.error(LOG_TAG, "Config var 'currentMode' is not SIM or DUAL");
            return;
        }


        if (Connection.PC.connect()) { //Try to connect

            GUI.init(); //Create GUI

            monitorForData();

            GUI.close();  //Close GUI
        }
    }

    private static void monitorForData() {
        while (Connection.PC.read()) { //Constantly check for new data
            Thread.yield();
        }

        Logger.error(LOG_TAG, "Lost connection to EV3");
    }
}