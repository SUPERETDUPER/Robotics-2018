/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import ev3.DataSender;
import ev3.hardware.ChassisBuilder;
import ev3.localization.RobotPoseProvider;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Controller {
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

        RobotPoseProvider.get().addMoveProvider(pilot);
        RobotPoseProvider.get().setPose(STARTING_POSE);

        navigator = new Navigator(pilot, RobotPoseProvider.get());

        new LineChecker().start();
    }

    @NotNull
    @Contract(pure = true)
    public static Controller get() {
        return controller;
    }

    public void waitForStop() {
        while (navigator.isMoving()) {
            RobotPoseProvider.get().sendCurrentPoseToPC();
            Thread.yield();
        }
    }

    void goTo(Pose pose) {
        goTo(pose.getX(), pose.getY(), pose.getHeading());
    }

    private void goTo(float x, float y, float heading) {
        navigator.goTo(x, y, normalize(heading));
        DataSender.sendPath(navigator.getPath());
        waitForStop();
    }

    void goTo(Point point) {
        goTo(point.x, point.y);
    }

    private void goTo(float x, float y) {
        navigator.goTo(x, y);
        DataSender.sendPath(navigator.getPath());
        waitForStop();
    }

    public static void init() {
    }

    @Contract(pure = true)
    private static float normalize(float heading) {
        while (heading >= 360) heading -= 360;
        while (heading < 0) heading += 360;
        return heading;
    }
}