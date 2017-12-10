import PC.Connection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import utils.Config;

public class EV3Run {

    private static final String LOG_TAG = EV3Run.class.getSimpleName();

    public static void main(String[] args) {
        if (Config.EV3_CONNECT_TO_PC) {
            Connection.EV3.connect();
        }

        //Controller.init();
        Button.ENTER.waitForPress();
        Sound.beep();
        //Controller.test();
    }
}