import PC.Connection;
import lejos.hardware.Button;
import navigation.CustomMCLPoseProvider;
import utils.Config;

public class EV3Run {

    private static final String LOG_TAG = EV3Run.class.getSimpleName();

    public static void main(String[] args) {

        if (Config.USING_PC) {
            if (!Connection.EV3.connect()) { //Try connecting to computer, stop if fails
                return;
            }
        }

        //Controller.init();
        Button.ENTER.waitForPress();
        Connection.EV3.sendMCLData();
        Button.ENTER.waitForPress();
        CustomMCLPoseProvider.get().getPose();
        Button.ENTER.waitForPress();
        //Controller.test();
    }
}