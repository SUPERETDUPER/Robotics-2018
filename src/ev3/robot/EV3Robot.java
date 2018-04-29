/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import common.logger.Logger;
import lejos.hardware.DeviceException;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import org.jetbrains.annotations.Nullable;

/**
 * Meta class holding all the robot's functions together
 * All the motors and sensors are created only when required (lazy initialization).
 * They are also created if the setup method is called in which case they are all created on their separate thread.
 */
public class EV3Robot  {
    private static final String LOG_TAG = EV3Robot.class.getSimpleName();

    private final EV3ColorSensors colorSensors = new EV3ColorSensors();
    private final EV3Brick brick = new EV3Brick();

    private volatile EV3LargeRegulatedMotor leftMotor;
    private volatile EV3LargeRegulatedMotor rightMotor;

    @Nullable
    private volatile EV3Arm arm;

    //Used by the synchronised blocks
    private final Object armLock = new Object();
    private final Object leftMotorLock = new Object();
    private final Object rightMotorLock = new Object();

    //Used by the setup() method and the isSetup() method
    private Thread armCreatorThread;
    private Thread leftMotorCreatorThread;
    private Thread rightMotorCreatorThread;


    public void setup() {
        armCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (armLock) {
                    try {
                        if (arm == null) {
                            arm = new EV3Arm();
                        }
                    } catch (IllegalArgumentException | DeviceException e) {
                        Logger.warning(LOG_TAG, "Could not create arm");
                    }
                }
            }
        };

        leftMotorCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (leftMotorLock) {
                    try {
                        if (leftMotor == null) {
                            leftMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT);
                        }
                    } catch (IllegalArgumentException | DeviceException e) {
                        Logger.warning(LOG_TAG, "Could not create arm");
                    }
                }
            }
        };

        rightMotorCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (rightMotorLock) {
                    try {
                        if (rightMotor == null) {
                            rightMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT);
                        }
                    } catch (IllegalArgumentException | DeviceException e) {
                        Logger.warning(LOG_TAG, "Could not create arm");
                    }
                }
            }
        };


        armCreatorThread.start();
        leftMotorCreatorThread.start();
        rightMotorCreatorThread.start();
        colorSensors.setup();
    }


    public boolean isSetup() {
        return !(armCreatorThread.isAlive() ||
                leftMotorCreatorThread.isAlive() ||
                rightMotorCreatorThread.isAlive() ||
                !colorSensors.isSetup());
    }

    public EV3Arm getArm() {
        if (arm == null) {
            synchronized (armLock) {
                if (arm == null) {
                    arm = new EV3Arm();
                }
            }
        }

        return arm;
    }


    public EV3ColorSensors getColorSensors() {
        return colorSensors;
    }

    public EV3Brick getBrick() {
        return brick;
    }

    public RegulatedMotor getLeftMotor() {
        if (leftMotor == null) {
            synchronized (leftMotorLock) {
                if (leftMotor == null) {
                    leftMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT);
                }
            }
        }

        return leftMotor;
    }

    public RegulatedMotor getRightMotor() {
        if (rightMotor == null) {
            synchronized (rightMotorLock) {
                if (rightMotor == null) {
                    rightMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT);
                }
            }
        }

        return rightMotor;
    }
}
