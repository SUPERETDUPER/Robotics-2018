/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.Config;
import common.logger.Logger;
import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.robot.Robot;
import ev3.robot.sim.SimRobot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Controller implements MoveListener, NavigationListener {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private static final double ANGULAR_ACCELERATION = 200;
    private static final double LINEAR_ACCELERATION = 400;
    private static final double ANGULAR_SPEED_PERCENT = 0.5;
    private static final double LINEAR_SPEED_PERCENT = 0.5;

    private static final Pose STARTING_POSE = new Pose(2152, 573, 180);

    private Navigator navigator;
    private PoseProvider robotPoseProvider;
    private final MyMovePilot pilot;
    private final SurfaceMap surfaceMap = new SurfaceMap(Config.currentMode == Config.Mode.SIM ? Config.PC_IMAGE_PATH : Config.EV3_IMAGE_PATH);

    public Controller(Robot robot) {
        Chassis chassis = robot.getChassis();

        pilot = new MyMovePilot(chassis);

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * LINEAR_SPEED_PERCENT);
        pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * ANGULAR_SPEED_PERCENT);

        pilot.addMoveListener(this);

        robotPoseProvider = chassis.getPoseProvider();
        robotPoseProvider.setPose(STARTING_POSE);
//        robotPoseProvider = new RobotPoseProvider(pilot, STARTING_POSE, surfaceMap);

        if (robot instanceof SimRobot) {
            ((SimRobot) robot).setPoseProvider(robotPoseProvider);
            ((SimRobot) robot).setSurfaceMap(surfaceMap);
        }

//        DataListener dataListener = ComManager.get().getDataListener();
//
//        if (dataListener != null) {
//            dataListener.attachToRobotPoseProvider(robotPoseProvider);
//        }

//        robotPoseProvider.startUpdater(robot.getColorSensors());

        navigator = new Navigator(pilot, robotPoseProvider);
        navigator.addNavigationListener(this);
    }

    @Contract(pure = true)
    public MyMovePilot getPilot() {
        return pilot;
    }

    public void followPath(@NotNull Path path) {
        for (int i = 0; i < path.size(); i++) {
            Waypoint waypoint = path.get(i);

            if (waypoint.isHeadingRequired()) {
                path.set(i, new Waypoint(waypoint.x, waypoint.y, normalize(waypoint.getHeading())));
            }
        }

        navigator.followPath(path);

        ComManager.get().sendTransmittable(navigator.getPath());
        waitForStop();
    }

    private void waitForStop() {
        while (navigator.isMoving()) {
            ComManager.get().sendTransmittable(robotPoseProvider.getPose());
            Thread.yield();
        }
    }


    @Contract(pure = true)
    private static double normalize(double heading) {
        while (heading >= 180) heading -= 360;
        while (heading < -180) heading += 360;
        return heading;
    }

    @Contract(pure = true)
    @NotNull
    public Pose getPose() {
        return robotPoseProvider.getPose();
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Started : " + move.toString());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Stopped : " + move.toString());
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