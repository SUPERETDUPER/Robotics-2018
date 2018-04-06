/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.localization.RobotPoseProvider;
import ev3.navigation.Controller;
import ev3.navigation.NavigatorBuilder;
import ev3.robot.Robot;
import ev3.robot.hardware.EV3Robot;
import ev3.robot.sim.SimRobot;
import lejos.robotics.navigation.MoveController;
import lejos.robotics.navigation.Navigator;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    private static Robot robot;
    private static Controller controller;

    public static void main(String[] args) {
        initialize();

        runMain();

        robot.getBrick().waitForUserConfirmation();  //When code is down this can be removed

        cleanUp();
    }

    private static void initialize() {
        //Connect to PC unless in SOLO

        if (Config.currentMode != Config.Mode.SOLO) {
            //ComManager.enable();
        }

        //Builds either a sim or an ev3 robot depending on config
        SurfaceMap surfaceMap;

        if (Config.currentMode == Config.Mode.SIM) {
            robot = new SimRobot();
            surfaceMap = new SurfaceMap(Config.PC_IMAGE_PATH);
            ((SimRobot) robot).setSurfaceMap(surfaceMap);
        } else {
            robot = new EV3Robot();
            surfaceMap = new SurfaceMap(Config.EV3_IMAGE_PATH);
        }

        MoveController pilot = NavigatorBuilder.getMoveProvider(robot.getChassis());
        RobotPoseProvider poseProvider = NavigatorBuilder.getPoseProvider(surfaceMap, pilot);

        if (Config.currentMode == Config.Mode.SIM){
            ((SimRobot) robot).setPoseProvider(poseProvider);
        }

        poseProvider.startUpdater(robot.getColorSensors());

        controller = new Controller(new Navigator(pilot, poseProvider));
    }

    private static void runMain() {
        new Brain(robot, controller).start();
    }

    private static void cleanUp() {
        ComManager comManager = ComManager.get();

        if (comManager != null) {
            comManager.stop();
        }
    }
}