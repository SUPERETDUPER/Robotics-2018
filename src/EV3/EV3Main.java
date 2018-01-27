package EV3;

import Common.Config;
import EV3.hardware.Brick;

class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        if (Config.usePC) {
            if (!DataSender.connect()) { //Try connecting to computer, stop if fails
                return;
            }
        }

        Controller controller = new Controller();
        Brick.waitForUserConfirmation();
        controller.goTo(1000, 100);
        controller.waitForCompletion();
        //controller.travel();
        Brick.waitForUserConfirmation();
    }
}