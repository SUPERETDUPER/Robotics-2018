package EV3;

import Common.Config;
import EV3.hardware.Brick;

class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        if (Config.usePC) {
            DataSender.connect();
        }

        //Controller controller = new Controller();
        //Brick.waitForUserConfirmation();
        //controller.goTo();
        //controller.travel();
        Brick.waitForUserConfirmation();
    }
}