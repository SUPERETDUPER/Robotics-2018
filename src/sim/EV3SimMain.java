package sim;

import PC.Connection;
import navigation.Controller;
import navigation.MyPoseProvider;
import utils.Config;
import utils.Logger;

import java.io.IOException;

public class EV3SimMain {

    private static final String LOG_TAG = EV3SimMain.class.getSimpleName();

    public static void main(String[] args) {
        Connection.runningOn = Connection.RUNNING_ON.EV3_SIM;

        if (Config.currentMode != Config.Mode.SIM) {
            Logger.error(LOG_TAG, "Config 'currentMode' var is not set to SIM");
            return;
        }

        if (!Connection.EV3.connect()) { //Try connecting to computer, stop if fails
            return;
        }

        MyPoseProvider.get().getPose();
        Controller.init();
        waitForConfirmation();
        Controller.travel(10);
        waitForConfirmation();
    }

    public static void waitForConfirmation() {
        try {
            Logger.info(LOG_TAG, "Press enter to continue");
            System.in.read();
        } catch (IOException e) {
            Logger.error(LOG_TAG, e.toString());
        }
    }
}