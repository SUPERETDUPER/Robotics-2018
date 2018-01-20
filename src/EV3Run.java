import PC.Connection;
import lejos.hardware.Button;
import navigation.MyPoseProvider;
import utils.Config;

class EV3Run {

    private static final String LOG_TAG = EV3Run.class.getSimpleName();

    public static void main(String[] args) {
        Connection.runningOnEV3 = true;

        if (Config.USING_PC) {
            if (!Connection.EV3.connect()) { //Try connecting to computer, stop if fails
                return;
            }
        }

        Button.ENTER.waitForPress();
        MyPoseProvider.get().getPose();
        Button.ENTER.waitForPress();
        //Controller.test();
    }
}