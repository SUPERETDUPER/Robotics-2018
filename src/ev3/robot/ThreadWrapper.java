/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

public class ThreadWrapper {
    private final MotorSensor motorSensor;

    private final Thread creatorThread = new Thread() {
        @Override
        public synchronized void run() {
            if (!motorSensor.isCreated()) {
                motorSensor.create();
            }
        }
    };

    public ThreadWrapper(MotorSensor motorSensor) {
        this.motorSensor = motorSensor;
    }

    public void setup() {
        creatorThread.start();
    }

    public boolean isSetupAlive() {
        return creatorThread.isAlive();
    }

    public MotorSensor get() {
        if (!motorSensor.isCreated()) {
            synchronized (this) {
                if (!motorSensor.isCreated()) {
                    motorSensor.create();
                }
            }
        }

        return motorSensor;
    }
}
