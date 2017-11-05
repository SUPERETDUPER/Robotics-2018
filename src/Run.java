import lejos.hardware.Button;
import lejos.hardware.Sound;
import navigation.Controller;

public class Run {

    private static final String LOG_TAG = Run.class.getSimpleName();

    public static void main(String[] args) {
        Controller.init();
        Button.ENTER.waitForPress();
        Sound.beep();
        Controller.test();
    }
}
