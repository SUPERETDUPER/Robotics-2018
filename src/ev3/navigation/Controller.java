/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.Config;
import common.logger.Logger;
import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.communication.DataListener;
import ev3.localization.RobotPoseProvider;
import ev3.robot.Robot;
import ev3.robot.sim.SimRobot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Controller implements MoveListener, NavigationListener {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private static final double ANGULAR_ACCELERATION = 200;
    private static final double LINEAR_ACCELERATION = 400;
    private static final double ANGULAR_SPEED_PERCENT = 0.5;
    private static final double LINEAR_SPEED_PERCENT = 0.5;

    private static final Pose STARTING_POSE = new Pose(2152, 573, 180);

    private final Navigator navigator;
    private final RobotPoseProvider poseProvider;

    public Controller(@NotNull Robot robot) {
        Chassis chassis = robot.getChassis();

        MyMovePilot pilot = new MyMovePilot(chassis);

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * LINEAR_SPEED_PERCENT);
        pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * ANGULAR_SPEED_PERCENT);

        pilot.addMoveListener(this);

        SurfaceMap surfaceMap = new SurfaceMap(Config.currentMode == Config.Mode.SIM ? Config.PC_IMAGE_PATH : Config.EV3_IMAGE_PATH);

        poseProvider = new RobotPoseProvider(surfaceMap, pilot);
        poseProvider.setPose(STARTING_POSE);

        if (robot instanceof SimRobot) {
            ((SimRobot) robot).setPoseProvider(poseProvider);
            ((SimRobot) robot).setSurfaceMap(surfaceMap);
        }

        ComManager comManager = ComManager.get();

        if (comManager != null) {
            DataListener dataListener = comManager.getDataListener();
            dataListener.attachToRobotPoseProvider(poseProvider);
        }

        poseProvider.startUpdater(robot.getColorSensors());

        navigator = new Navigator(pilot, poseProvider);
        navigator.addNavigationListener(this);
    }

    public void followPath(@NotNull Path path) {
        for (int i = 0; i < path.size(); i++) {
            Waypoint waypoint = path.get(i);

            if (waypoint.isHeadingRequired()) {
                path.set(i, new Waypoint(waypoint.x, waypoint.y, normalize(waypoint.getHeading())));
            }
        }

        navigator.followPath(path);

        ComManager comManager = ComManager.get();
        if (comManager != null) {
            comManager.sendTransmittable(navigator.getPath());
        }

        waitForStop();
    }

    private void waitForStop() {
        while (navigator.isMoving()) {
            ComManager comManager = ComManager.get();
            if (comManager != null) {
                comManager.sendTransmittable(poseProvider.getPose());
            }

            Thread.yield();
        }
    }


    @Contract(pure = true)
    private static double normalize(double heading) {
        while (heading > 180) heading -= 360;
        while (heading <= -180) heading += 360;
        return heading;
    }

    @Contract(pure = true)
    @Nullable
    public Pose getPose() {
        return poseProvider.getPose();
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
//        Logger.info(LOG_TAG, "Started : " + move.toString());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
//        Logger.info(LOG_TAG, "Stopped : " + move.toString());
    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "At waypoint : " + waypoint + " pose : " + pose.toString());
    }

    @Override
    public void pathComplete(Waypoint waypoint, Pose pose, int i) {
//        Logger.info(LOG_TAG, "Path complete : " + waypoint + " pose : " + pose.toString());
    }

    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "Path interrupted : " + waypoint + " pose : " + pose.toString());
    }
}