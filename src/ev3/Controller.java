/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import ev3.robot.EV3Robot;
import lejos.robotics.RegulatedMotor;

public class Controller {
    private final EV3Robot robot;

    public Controller(EV3Robot robot) {
        this.robot = robot;
        robot.getLeftMotor().synchronizeWith(new RegulatedMotor[]{robot.getRightMotor()});
    }

    public void followLineLeft() {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().forward();
        robot.getRightMotor().forward();
        robot.getLeftMotor().startSynchronization();

        do {
            int error = (int) (10 * (0.5F - robot.getColorSensors().getColorSurfaceLeft()));


            robot.getLeftMotor().startSynchronization();
            robot.getLeftMotor().setSpeed(200 + error);
            robot.getRightMotor().setSpeed(200 - error);
            robot.getLeftMotor().endSynchronization();

        } while (robot.getColorSensors().getColorSurfaceRight() > 0.2);
    }
}
