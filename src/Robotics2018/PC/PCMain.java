package Robotics2018.PC;

import Robotics2018.PC.GUI.GUI;
import Robotics2018.Config;
import Robotics2018.utils.Logger;

class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(String[] args) {
        Connection.isEV3 = false;

        if (!Config.isDual) {
            Logger.error(LOG_TAG, "Config var 'isDual' isFalse");
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