package Robotics2018;

import Robotics2018.PC.Connection;
import Robotics2018.utils.Helper;
import Robotics2018.navigation.Controller;

class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        Connection.isEV3 = true;

        if (Config.isDual) {
            if (!Connection.EV3.connect()) { //Try connecting to computer, stop if fails
                return;
            }
        }

        Controller.init();
        Controller.get().travel(100);
        Helper.waitForConfirmation();
    }
}