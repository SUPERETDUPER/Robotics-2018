package PC;

import navigation.CustomMCLPoseProvider;
import utils.Config;
import utils.logger.Logger;

import javax.swing.*;

public class PCRun {

    private static final String LOG_TAG = PCRun.class.getSimpleName();


    public static void main(String[] args) {

        if (Config.EV3_CONNECT_TO_PC) {
            Logger.print(Logger.typeInfo, LOG_TAG, "Connecting...");
            Connection.PC.connect();
            Logger.print(Logger.typeInfo, LOG_TAG, "Done connecting");
        }

        CustomMCLPoseProvider.get().getPose();
        JFrame window = new JFrame();
        window.getContentPane().add(new MapGUI());
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
