/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.robotics.RegulatedMotor;

/**
 * Meta class holding all the robot's functions together
 * All the motors and sensors are created only when required (lazy initialization).
 * They are also created if the setup method is called in which case they are all created on their separate thread.
 */
public class EV3Robot {
    private static final String LOG_TAG = EV3Robot.class.getSimpleName();

    private final EV3Brick brick = new EV3Brick();

    private final ThreadWrapper<EV3LargeMotor> leftMotor = new ThreadWrapper<>(new EV3LargeMotor(Ports.MOTOR_LEFT));
    private final ThreadWrapper<EV3LargeMotor> rightMotor = new ThreadWrapper<>(new EV3LargeMotor(Ports.MOTOR_RIGHT));
    private final ThreadWrapper<EV3Arm> arm = new ThreadWrapper<>(new EV3Arm());
    private final ThreadWrapper<EV3Claw> claw = new ThreadWrapper<>(new EV3Claw());
    private final ThreadWrapper<EV3ColorSensor> surfaceLeft = new ThreadWrapper<>(new EV3ColorSensor(Ports.SENSOR_COLOR_SURFACE_LEFT));
    private final ThreadWrapper<EV3ColorSensor> surfaceRight = new ThreadWrapper<>(new EV3ColorSensor(Ports.SENSOR_COLOR_SURFACE_RIGHT));
    private final ThreadWrapper<EV3ColorSensor> boat = new ThreadWrapper<>(new EV3ColorSensor(Ports.SENSOR_COLOR_BOAT));

    public void setup() {
        arm.setup();
        leftMotor.setup();
        rightMotor.setup();
        surfaceLeft.setup();
        surfaceRight.setup();
        boat.setup();
        claw.setup();
    }


    public boolean stillSettingUp() {
        return arm.isSetupAlive() ||
                leftMotor.isSetupAlive() ||
                rightMotor.isSetupAlive() ||
                surfaceLeft.isSetupAlive() ||
                surfaceRight.isSetupAlive() ||
                boat.isSetupAlive() ||
                claw.isSetupAlive();
    }

    public EV3Arm getArm() {
        return arm.get();
    }

    public EV3Claw getClaw() {
        return claw.get();
    }

    public EV3Brick getBrick() {
        return brick;
    }

    public RegulatedMotor getLeftMotor() {
        return leftMotor.get().get();
    }

    public RegulatedMotor getRightMotor() {
        return rightMotor.get().get();
    }

    public float getColorSurfaceLeft() {
        return surfaceLeft.get().getRed();
    }

    public float getColorSurfaceRight() {
        return surfaceRight.get().getRed();
    }

    public int getColorBoat() {
        return boat.get().getColor();
    }
}
