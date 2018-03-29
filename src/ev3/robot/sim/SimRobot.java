/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import common.mapping.SurfaceMap;
import ev3.localization.RobotPoseProvider;
import ev3.robot.*;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.Pose;

import java.io.IOException;

public class SimRobot implements Robot {
    private static final String LOG_TAG = SimRobot.class.getSimpleName();

    private static final double WHEEL_OFFSET = 81.5;
    private static final double WHEEL_DIAMETER = 55.9;

    @Override
    public Arm getArm() {
        return new SimArm();
    }

    @Override
    public Chassis getChassis() {
        return Util.buildChassis(new SimMotor("leftMotor"), new SimMotor("rightMotor"), WHEEL_DIAMETER, WHEEL_OFFSET);
    }

    @Override
    public Paddle getPaddle() {
        return new SimPaddle();
    }

    @Override
    public ColorSensors getColorSensors() {
        return new SimColorSensors();
    }

    @Override
    public Brick getBrick() {
        return new SimBrick();
    }

    private class SimBrick implements Brick {
        @Override
        public void waitForUserConfirmation() {
            try {
                System.out.println("Press enter to continue");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        }
    }

    private class SimPaddle implements Paddle {
        @Override
        public void moveBlockOffConveyor(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Moving block of conveyor");
        }

        @Override
        public void hitBlock(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Hitting block");
        }
    }

    private class SimColorSensors implements ColorSensors {
        @Override
        public int getColorSurfaceLeft() {
            Pose currentPose = RobotPoseProvider.get().getPose();
            return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
        }

        @Override
        public int getColorSurfaceRight() {
            Pose currentPose = RobotPoseProvider.get().getPose();
            return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
        }

        @Override
        public int getColorContainer() {
            Pose currentPose = RobotPoseProvider.get().getPose();
            return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
        }

        @Override
        public int getColorBoat() {
            Pose currentPose = RobotPoseProvider.get().getPose();
            return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
        }
    }

    private class SimArm implements Arm {
        @Override
        public void goToBoat(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Moving arm to boat");
        }

        @Override
        public void goToFoodIn(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Moving arm to food in");
        }

        @Override
        public void goToFoodOut(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Moving arm to food out");
        }

        @Override
        public void goToFoodHanging(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Moving arm to food hanging");
        }

        @Override
        public void goToTempReg(boolean immediateReturn) {
            Logger.info(LOG_TAG, "Moving arm to temp reg");
        }
    }
}
