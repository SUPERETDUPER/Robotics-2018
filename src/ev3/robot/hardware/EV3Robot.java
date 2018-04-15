/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import common.logger.Logger;
import ev3.navigation.NavigatorBuilder;
import ev3.robot.Robot;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.chassis.Chassis;
import org.jetbrains.annotations.Nullable;

/**
 * Meta class holding all the robot's functions together
 * All the motors and sensors are created only when required (lazy initialization).
 * They are also created if the setup method is called in which case they are all created on their separate thread.
 */
public class EV3Robot implements Robot {
    private static final String LOG_TAG = EV3Robot.class.getSimpleName();

    private final EV3ColorSensors colorSensors = new EV3ColorSensors();
    private final EV3Brick brick = new EV3Brick();

    @Nullable
    private volatile EV3Arm arm;
    @Nullable
    private volatile EV3Paddle paddle;
    @Nullable
    private volatile Chassis chassis;

    //Used by the synchronised blocks
    private final Object armLock = new Object();
    private final Object chassisLock = new Object();
    private final Object paddleLock = new Object();

    //Used by the setup() method and the isSetup() method
    private Thread armCreatorThread;
    private Thread paddleCreatorThread;
    private Thread chassisCreatorThread;

    @Override
    public void setup() {
        armCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (armLock) {
                    try {
                        if (arm == null) {
                            arm = new EV3Arm();
                        }
                    } catch (IllegalArgumentException e) {
                        Logger.warning(LOG_TAG, "Could not create arm");
                    }
                }
            }
        };

        paddleCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (paddleLock) {
                    try {
                        if (paddle == null) {
                            paddle = new EV3Paddle();
                        }
                    } catch (IllegalArgumentException e) {
                        Logger.warning(LOG_TAG, "Could not create paddle");
                    }
                }
            }
        };

        chassisCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (chassisLock) {
                    if (chassis == null) {
                        try {
                            chassis = NavigatorBuilder.buildChassis(
                                    new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT),
                                    new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT)
                            );
                        } catch (IllegalArgumentException e) {
                            Logger.warning(LOG_TAG, "Could not create chassis");
                        }
                    }
                }
            }
        };

        armCreatorThread.start();
        paddleCreatorThread.start();
        chassisCreatorThread.start();
        colorSensors.setup();
    }

    @Override
    public boolean isSetup() {
        return !(paddleCreatorThread.isAlive() || chassisCreatorThread.isAlive() || armCreatorThread.isAlive() || !colorSensors.isSetup());
    }

    @Override
    public Arm getArm() {
        if (arm == null) {
            synchronized (armLock) {
                if (arm == null) {
                    arm = new EV3Arm();
                }
            }
        }

        return arm;
    }

    @Override
    public Chassis getChassis() {
        if (chassis == null) {
            synchronized (chassisLock) {
                if (chassis == null) {
                    chassis = NavigatorBuilder.buildChassis(
                            new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT),
                            new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT)
                    );
                }
            }
        }

        return chassis;
    }

    @Override
    public Paddle getPaddle() {
        if (paddle == null) {
            synchronized (paddleLock) {
                if (paddle == null) {

                    paddle = new EV3Paddle();
                }
            }
        }

        return paddle;
    }

    @Override
    public ColorSensors getColorSensors() {
        return colorSensors;
    }

    @Override
    public Brick getBrick() {
        return brick;
    }
}
