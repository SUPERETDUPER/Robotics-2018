/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.TransmittableType;
import ev3.communication.ComManager;
import ev3.localization.RobotPoseProvider;
import ev3.robot.Robot;
import ev3.robot.sim.SimRobot;
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

    private Navigator navigator;
    private RobotPoseProvider robotPoseProvider;

    private Controller() {

    }

    public void init(@NotNull Robot robot) {
        MyMovePilot pilot = new MyMovePilot(robot.getChassis());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);

        robotPoseProvider = new RobotPoseProvider(pilot, STARTING_POSE);

        if (robot instanceof SimRobot) {
            ((SimRobot) robot).setPoseProvider(robotPoseProvider);
        }

        ComManager.getDataListener().attachToRobotPoseProvider(robotPoseProvider);

        robotPoseProvider.startUpdater(robot.getColorSensors());

        navigator = new Navigator(pilot, robotPoseProvider);
    }

    @NotNull
    @Contract(pure = true)
    public static Controller get() {
        return controller;
    }

    public void waitForStop() {
        while (navigator.isMoving()) {
            robotPoseProvider.sendCurrentPoseToPC();
            Thread.yield();
        }
    }

    void goTo(@NotNull Pose pose) {
        goTo(pose.getX(), pose.getY(), pose.getHeading());
    }

    private void goTo(float x, float y, float heading) {
        navigator.goTo(x, y, normalize(heading));
        ComManager.getDataSender().sendTransmittable(TransmittableType.PATH, navigator.getPath());
        waitForStop();
    }

    void goTo(@NotNull Point point) {
        goTo(point.x, point.y);
    }

    private void goTo(float x, float y) {
        navigator.goTo(x, y);
        ComManager.getDataSender().sendTransmittable(TransmittableType.PATH, navigator.getPath());
        waitForStop();
    }

    @Contract(pure = true)
    private static float normalize(float heading) {
        while (heading >= 360) heading -= 360;
        while (heading < 0) heading += 360;
        return heading;
    }

    @Contract(pure = true)
    @NotNull
    public Pose getPose() {
        return robotPoseProvider.getPose();
    }
}