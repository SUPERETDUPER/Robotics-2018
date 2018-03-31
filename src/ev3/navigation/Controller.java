/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import ev3.communication.ComManager;
import ev3.robot.Robot;
import ev3.robot.sim.SimRobot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Controller {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;
    private static final Pose STARTING_POSE = new Pose(2242, 573, 180);

    private Navigator navigator;
    private PoseProvider robotPoseProvider;

    public Controller(Robot robot) {
        Chassis chassis = robot.getChassis();

        MyMovePilot pilot = new MyMovePilot(chassis);

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);

        robotPoseProvider = chassis.getPoseProvider();
        robotPoseProvider.setPose(STARTING_POSE);

        if (robot instanceof SimRobot) {
            ((SimRobot) robot).setPoseProvider(robotPoseProvider);
        }

//        ComManager.getDataListener().attachToRobotPoseProvider(robotPoseProvider);

//        robotPoseProvider.startUpdater(robot.getColorSensors());

        navigator = new Navigator(pilot, robotPoseProvider);
    }

    public void followPath(Path path) {
        for (int i = 0; i < path.size(); i++) {
            Waypoint waypoint = path.get(i);
            path.set(i, new Waypoint(waypoint.x, waypoint.y, normalize(waypoint.getHeading())));
        }


        navigator.followPath(path);

        ComManager.sendTransmittable(navigator.getPath());
        waitForStop();
    }

    private void waitForStop() {
        while (navigator.isMoving()) {
            ComManager.sendTransmittable(robotPoseProvider.getPose());
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
}