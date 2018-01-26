package EV3.hardware;

import Common.Config;
import Common.utils.Logger;
import lejos.hardware.Button;

import java.io.IOException;

public final class Brick {
    private static final String LOG_TAG = Brick.class.getSimpleName();

    public static void waitForUserConfirmation() {
        if (!Config.useSimulator) {
            Button.ENTER.waitForPress();
        } else {
            try {
                Logger.info(LOG_TAG, "Press enter to continue");
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        }
    }
}