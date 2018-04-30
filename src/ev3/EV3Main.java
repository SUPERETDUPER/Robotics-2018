/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import common.ConnectionUtil;
import common.logger.LogMessage;
import common.logger.LogMessageListener;
import common.logger.Logger;
import ev3.robot.EV3Robot;

final class EV3Main {
    // --Commented out by Inspection (25/04/18 8:37 PM):private static final String LOG_TAG = EV3Main.class.getSimpleName();

    private static EV3Robot robot;
    private static Controller controller;

    public static void main(String[] args) {
        initialize();

        robot.getBrick().beep();

//        robot.getBrick().waitForUserConfirmation();

        runMain();

//        robot.getBrick().waitForUserConfirmation();  //Uncomment if you want the user to need to press enter before the program closes
    }

    private static void initialize() {
        //Connect to PC and send log messages
        if (Config.sendLogToPC) {
            //Creates a data sender
            final PCDataSender dataSender = new PCDataSender(ConnectionUtil.createOutputStream(
                    ConnectionUtil.createServerSocket(Config.PORT_TO_CONNECT_ON_EV3)
            ));

            //Register the data sender with the Logger
            Logger.setListener(new LogMessageListener() {
                @Override
                public void notifyLogMessage(LogMessage logMessage) {
                    dataSender.sendLogMessage(logMessage);
                }
            });
        }

        robot = new EV3Robot();

        robot.setup(); //Creates the ports in separate threads (which takes time)

        controller = new Controller(robot);

        //Waits for all the sensors to load
        if (Config.WAIT_FOR_SENSORS) {
            while (!robot.isSetup()) Thread.yield();
        }
    }

    private static void runMain() {
        new Brain(robot, controller).start();
    }
}