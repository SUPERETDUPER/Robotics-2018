/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import ev3.communication.ComManager;
import ev3.robot.Robot;
import ev3.robot.hardware.EV3Robot;
import ev3.robot.sim.SimRobot;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    private static Robot robot;

    public static void main(String[] args) {
        initialize();

        runMain();

        robot.getBrick().waitForUserConfirmation();

        cleanUp();
    }

    private static void initialize() {
        if (Config.currentMode == Config.Mode.DUAL || Config.currentMode == Config.Mode.SIM) {
            ComManager.startCommunication();
        }

        if (Config.currentMode == Config.Mode.SIM) {
            robot = new SimRobot();
        } else {
            robot = new EV3Robot();
        }
    }

    private static void runMain() {
        Brain.start(robot);
//        robot.getPaddle().hitBlock(true);
    }

    private static void cleanUp() {
        ComManager.stop();
    }
}