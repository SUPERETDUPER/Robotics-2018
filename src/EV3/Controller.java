/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3;

import Common.Logger;
import EV3.hardware.ChassisBuilder;
import EV3.localization.RobotPoseProvider;
import EV3.navigation.LineChecker;
import EV3.navigation.MyMovePilot;
import EV3.navigation.Readings;
import lejos.robotics.navigation.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();
    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;
    private static final Pose STARTING_POSE = new Pose(500, 100, 0);

    private static final Controller controller = new Controller();

    @NotNull
    private final Navigator navigator;

    private Controller() {
        MyMovePilot pilot = new MyMovePilot(ChassisBuilder.getChassis());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * 0.8D);
        pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * 0.8D);
        pilot.addMoveListener(this);

        RobotPoseProvider.get().addMoveProvider(pilot);
        RobotPoseProvider.get().setPose(STARTING_POSE);

        navigator = new Navigator(pilot, RobotPoseProvider.get());
        navigator.addNavigationListener(this);
        navigator.singleStep(true);

        new LineChecker().start();
    }

    @NotNull
    @Contract(pure = true)
    public static Controller get() {
        return controller;
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        DataSender.sendPath(navigator.getPath());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
    }

    @Override
    public void pathComplete(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "Path complete");
    }

    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "pathInterrupted");
    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {
//        Logger.info(LOG_TAG, "At Waypoint");
    }

    void testMethod() {
        navigator.addWaypoint(new Waypoint(1200, 400));
        navigator.addWaypoint(new Waypoint(300, 1000));
        navigator.followPath();

        while (navigator.isMoving()) {
            RobotPoseProvider.get().updatePC();
            Thread.yield();
        }
        Logger.info(LOG_TAG, RobotPoseProvider.get().getPose().toString());
    }

    public void update(@NotNull Readings readings) {
        RobotPoseProvider.get().update(readings);
    }
}