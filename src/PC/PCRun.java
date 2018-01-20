package PC;

import utils.Config;
import utils.Logger;

class PCRun {

    private static final String LOG_TAG = PCRun.class.getSimpleName();

    public static void main(String[] args) {

        if (!Config.USING_PC) {
            Logger.error(LOG_TAG, "Config var USING_PC is false");
            return;
        }


        if (Connection.PC.connect()) {

            GUI.init();

            for (int failedReads = 0; failedReads < 3; Thread.yield()) {
                if (Connection.PC.read()) {// Try to read
                    failedReads = 0;
                } else {
                    failedReads++;
                }
            }

            Logger.error(LOG_TAG, "Lost connection to EV3");

            GUI.get().close();
        }
    }
}
