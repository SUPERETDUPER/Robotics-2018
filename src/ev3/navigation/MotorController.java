/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;
import lejos.robotics.RegulatedMotor;

public class MotorController {
    private static final String LOG_TAG = MotorController.class.getSimpleName();

    private final RegulatedMotor leftMotor;
    private final RegulatedMotor rightMotor;

    public MotorController(RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;

        leftMotor.synchronizeWith(new RegulatedMotor[]{rightMotor});
    }

    int tachoLeft() {
        return leftMotor.getTachoCount();
    }

    int tachoRight() {
        return rightMotor.getTachoCount();
    }

    void rotate(int leftDegrees, int rightDegrees, boolean immediateReturn) {
        leftMotor.startSynchronization();
        leftMotor.rotate(leftDegrees);
        rightMotor.rotate(rightDegrees);
        leftMotor.endSynchronization();

        Logger.debug(LOG_TAG, "Turning left: " + leftDegrees + " right: " + rightDegrees + " immediateReturn " + immediateReturn);

        if (!immediateReturn) waitComplete();
    }

    void forward() {
        leftMotor.startSynchronization();
        leftMotor.forward();
        rightMotor.forward();
        leftMotor.startSynchronization();
    }

    void setSpeed(int leftSpeed, int rightSpeed) {
        leftMotor.startSynchronization();
        leftMotor.setSpeed(leftSpeed);
        rightMotor.setSpeed(rightSpeed);
        leftMotor.endSynchronization();

        Logger.debug(LOG_TAG, "Set speed. Left: " + leftSpeed + " right: " + rightSpeed);
    }

    void stop() {
        leftMotor.startSynchronization();
        leftMotor.stop();
        rightMotor.stop();
        leftMotor.startSynchronization();
    }

    void waitComplete() {
        leftMotor.waitComplete();
        rightMotor.waitComplete();
    }
}
