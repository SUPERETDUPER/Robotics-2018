/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;
import lejos.robotics.RegulatedMotor;

class MotorController {
    private static final String LOG_TAG = MotorController.class.getSimpleName();

    private final RegulatedMotor leftMotor;
    private final RegulatedMotor rightMotor;

    MotorController(RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;

        leftMotor.synchronizeWith(new RegulatedMotor[]{rightMotor});
    }

    void rotate(int leftDegrees, int rightDegrees, Integer speedLeft, Integer speedRight, boolean immediateReturn) {
        leftMotor.startSynchronization();
        if (speedLeft != null) leftMotor.setSpeed(speedLeft);
        if (speedRight != null) rightMotor.setSpeed(speedRight);
        leftMotor.rotate(leftDegrees);
        rightMotor.rotate(rightDegrees);
        leftMotor.endSynchronization();
        Logger.debug(LOG_TAG, "Turning left: " + leftDegrees + " right: " + rightDegrees + " immediateReturn " + immediateReturn);

        if (immediateReturn) return;
        leftMotor.waitComplete();
        rightMotor.waitComplete();
    }

    void forward(int speed) {
        leftMotor.startSynchronization();
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        leftMotor.forward();
        rightMotor.forward();
        leftMotor.startSynchronization();
    }

    void setSpeed(int leftSpeed, int rightSpeed) {
        leftMotor.startSynchronization();
        leftMotor.setSpeed(leftSpeed);
        rightMotor.setSpeed(rightSpeed);
        leftMotor.endSynchronization();
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
