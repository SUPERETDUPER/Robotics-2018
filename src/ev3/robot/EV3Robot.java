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

    private final CustomEV3LargeMotor leftMotor = new CustomEV3LargeMotor(Ports.MOTOR_LEFT);
    private final CustomEV3LargeMotor rightMotor = new CustomEV3LargeMotor(Ports.MOTOR_RIGHT);
    private final ThreadWrapper arm = new ThreadWrapper(new EV3Arm());
    private final ThreadWrapper claw = new ThreadWrapper(new EV3Claw());
    private final CustomEV3ColorSensor surfaceLeft = new CustomEV3ColorSensor(Ports.SENSOR_COLOR_SURFACE_LEFT);
    private final CustomEV3ColorSensor surfaceRight = new CustomEV3ColorSensor(Ports.SENSOR_COLOR_SURFACE_RIGHT);
    private final CustomEV3ColorSensor boat = new CustomEV3ColorSensor(Ports.SENSOR_COLOR_BOAT);

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
                leftMotor.stillSettingUp() ||
                rightMotor.stillSettingUp() ||
                surfaceLeft.stillSettingUp() ||
                surfaceRight.stillSettingUp() ||
                boat.stillSettingUp() ||
                claw.isSetupAlive();
    }

    public EV3Arm getArm() {
        return (EV3Arm) arm.get();
    }

    public EV3Claw getClaw() {
        return (EV3Claw) claw.get();
    }

    public EV3Brick getBrick() {
        return brick;
    }

    public RegulatedMotor getLeftMotor() {
        return leftMotor.get();
    }

    public RegulatedMotor getRightMotor() {
        return rightMotor.get();
    }

    public float getColorSurfaceLeft() {
        return surfaceLeft.getRed();
    }

    public float getColorSurfaceRight() {
        return surfaceRight.getRed();
    }

    public int getColorBoat() {
        return boat.getColor();
    }
}
