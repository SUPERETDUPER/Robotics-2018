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

    public static void main(String[] args) {
        if (Config.currentMode == Config.Mode.DUAL || Config.currentMode == Config.Mode.SIM) {
            boolean sucess = ComManager.build();

            if (!sucess){
                return;
            }
        }

        Robot robot;

        if (Config.currentMode == Config.Mode.SIM) {
            robot = new SimRobot();
        } else {
            robot = new EV3Robot();
        }

        Brain.start(robot);
//        EV3Brick.waitForUserConfirmation(); //And wait for complete
    }
}