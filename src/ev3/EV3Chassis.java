/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import lejos.robotics.RegulatedMotor;

class EV3Chassis {
    private static final String LOG_TAG = EV3Chassis.class.getSimpleName();

    private final RegulatedMotor leftMotor;
    private final RegulatedMotor rightMotor;

    private final int speed;

    EV3Chassis(RegulatedMotor leftMotor, RegulatedMotor rightMotor, int speed) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.speed = speed;

        leftMotor.synchronizeWith(new RegulatedMotor[]{rightMotor});
    }

    void move(int amount) {
        rotate(amount, amount);
    }

    void move(int amount, boolean immediateReturn) {
        rotate(amount, amount, immediateReturn);
    }

    void rotate(int left, int right) {
        rotate(left, right, false);
    }

    void rotate(int left, int right, boolean immediateReturn) {
        leftMotor.startSynchronization();
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        leftMotor.rotate(left);
        rightMotor.rotate(right);
        leftMotor.endSynchronization();
        Logger.debug(LOG_TAG, "Turning left: " + left + " right: " + right + " immediateReturn " + immediateReturn);

        if (immediateReturn) return;
        leftMotor.waitComplete();
        rightMotor.waitComplete();
    }

    void setSpeed(int leftSpeed, int rightSpeed) {
        leftMotor.startSynchronization();
        leftMotor.setSpeed(leftSpeed);
        rightMotor.setSpeed(rightSpeed);
        leftMotor.endSynchronization();
    }

    void forward() {
        leftMotor.startSynchronization();
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        leftMotor.forward();
        rightMotor.forward();
        leftMotor.startSynchronization();
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
