/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.navigation;

import Common.Logger;
import EV3.DataSender;
import EV3.hardware.ChassisBuilder;
import EV3.localization.RobotPoseProvider;
import lejos.robotics.navigation.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();
    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;
    private static final Pose STARTING_POSE = new Pose(2242, 573, 180);

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
    }
    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {
    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {
    }

    public void waitForStop() {
        while (navigator.isMoving()) {
            RobotPoseProvider.get().updatePC();
            Thread.yield();
        }
    }

    @NotNull
    public Navigator getNavigator() {
        return navigator;
    }

    public static void init() {
    }
}