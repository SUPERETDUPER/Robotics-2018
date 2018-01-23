import PC.Connection;
import lejos.hardware.Button;
import navigation.Controller;
import navigation.MyPoseProvider;
import utils.Config;
import utils.Logger;

class EV3Main {

    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        Connection.runningOn = Connection.RUNNING_ON.EV3;

        if (Config.currentMode != Config.Mode.DUAL && Config.currentMode != Config.Mode.STANDALONE) {
            Logger.error(LOG_TAG, "Config 'currentMode' var is not set to STANDALONE or DUAL");
            return;
        }

        if (Config.currentMode == Config.Mode.DUAL) {
            if (!Connection.EV3.connect()) { //Try connecting to computer, stop if fails
                return;
            }
        }

        MyPoseProvider.get().getPose();
        Button.ENTER.waitForPress();
        Controller.travel(10);
        Button.ENTER.waitForPress();
    }
}