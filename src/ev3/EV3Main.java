/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import common.ConnectionUtil;
import common.RunModes;
import ev3.communication.ComManager;
import ev3.robot.Robot;
import ev3.robot.hardware.EV3Robot;

final class EV3Main {
    // --Commented out by Inspection (25/04/18 8:37 PM):private static final String LOG_TAG = EV3Main.class.getSimpleName();

    private static Robot robot;

    public static void main(String[] args) {
        initialize();

        runMain();

//        robot.getBrick().waitForUserConfirmation();  //Uncomment if you want the user to need to press enter before the program closes

        cleanUp();
    }

    private static void initialize() {
        //Connect to PC unless in SOLO
        if (Config.currentMode == RunModes.DEBUG) {
            ComManager.enable(
                    ConnectionUtil.createOutputStream(
                            ConnectionUtil.createServerSocket(Config.PORT_TO_CONNECT_ON_EV3))
            );
        }


        robot = new EV3Robot();
        robot.setup();

        //Waits for all the sensors to load
        if (Config.WAIT_FOR_SENSORS) {
            while (!robot.isSetup()) Thread.yield();
            robot.getBrick().beep();
        }
    }

    private static void runMain() {
        new Brain(robot).start();
    }

    private static void cleanUp() {
        ComManager.stop();
    }
}