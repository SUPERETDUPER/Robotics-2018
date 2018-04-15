/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import common.ConnectionUtil;
import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.localization.RobotPoseProvider;
import ev3.navigation.Controller;
import ev3.navigation.MyMovePilot;
import ev3.navigation.MyNavigator;
import ev3.navigation.NavigatorBuilder;
import ev3.robot.Robot;
import ev3.robot.hardware.EV3Robot;
import ev3.robot.sim.SimRobot;
import lejos.robotics.localization.PoseProvider;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    private static Robot robot;
    private static Controller controller;

    public static void main(String[] args) {
        initialize();

        runMain();

//        robot.getBrick().waitForUserConfirmation();  //Uncomment if you want the user to need to press enter before the program closes

        cleanUp();
    }

    private static void initialize() {
        //Connect to PC unless in SOLO
        if (Config.currentMode != Config.Mode.SOLO) {
            ComManager.enable(ConnectionUtil.createOutputStream(ConnectionUtil.createServerSocket(Config.PORT_TO_CONNECT_ON_EV3)));
        }

        //Builds either a sim or an ev3 robot depending on config
        SurfaceMap surfaceMap;

        if (Config.currentMode == Config.Mode.SIM) {
            surfaceMap = new SurfaceMap(Config.PC_IMAGE_PATH);
            robot = new SimRobot(surfaceMap);
        } else {
            robot = new EV3Robot();
            surfaceMap = new SurfaceMap(Config.EV3_IMAGE_PATH);
        }

        robot.setup();

        //Waits for all the sensors to load
        if (Config.WAIT_FOR_SENSORS) {
            while (!robot.isSetup()) Thread.yield();
            robot.getBrick().beep();
        }

        MyMovePilot pilot = NavigatorBuilder.buildMoveProvider(robot.getChassis());
        PoseProvider poseProvider = NavigatorBuilder.buildPoseProvider(surfaceMap, pilot);

        if (Config.currentMode == Config.Mode.SIM){
            ((SimRobot) robot).setPoseProvider(poseProvider);
        }

        if (poseProvider instanceof RobotPoseProvider) {
            ((RobotPoseProvider) poseProvider).startUpdater(robot.getColorSensors());
        }

        controller = new Controller(new MyNavigator(pilot, poseProvider));
    }

    private static void runMain() {
        new Brain(robot, controller).start();
    }

    private static void cleanUp() {
        ComManager.stop();
    }
}