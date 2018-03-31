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

//        robot.getBrick().waitForUserConfirmation();  //When code is down this can be removed

        cleanUp();
    }

    private static void initialize() {
        //Connect to PC unless in SOLO
        if (Config.currentMode != Config.Mode.SOLO) {
            ComManager.get().enable();
        }

        //Builds either a sim or an ev3 robot depending on config
        robot = Config.currentMode == Config.Mode.SIM ? new SimRobot() : new EV3Robot();
    }

    private static void runMain() {
//        Path path = new Path();
//        path.add(new Waypoint(242, 573));
//        robot.getController().followPath(path);
        robot.getController().getPilot().rotate(1080);
//        new Brain(robot).start();
//        robot.getPaddle().hitBlock(true);
    }

    private static void cleanUp() {
        ComManager.get().stop();
    }
}