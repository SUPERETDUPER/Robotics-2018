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

    private final ThreadWrapper leftMotor = new ThreadWrapper(new EV3LargeMotor(Ports.MOTOR_LEFT));
    private final ThreadWrapper rightMotor = new ThreadWrapper(new EV3LargeMotor(Ports.MOTOR_RIGHT));
    private final ThreadWrapper arm = new ThreadWrapper(new EV3Arm());
    private final ThreadWrapper claw = new ThreadWrapper(new EV3Claw());
    private final ThreadWrapper surfaceLeft = new ThreadWrapper(new EV3ColorSensor(Ports.SENSOR_COLOR_SURFACE_LEFT));
    private final ThreadWrapper surfaceRight = new ThreadWrapper(new EV3ColorSensor(Ports.SENSOR_COLOR_SURFACE_RIGHT));
    private final ThreadWrapper boat = new ThreadWrapper(new EV3ColorSensor(Ports.SENSOR_COLOR_BOAT));

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
        return (EV3Arm) arm.get();
    }

    public EV3Claw getClaw() {
        return (EV3Claw) claw.get();
    }

    public EV3Brick getBrick() {
        return brick;
    }

    public RegulatedMotor getLeftMotor() {
        return ((EV3LargeMotor) leftMotor.get()).get();
    }

    public RegulatedMotor getRightMotor() {
        return ((EV3LargeMotor) rightMotor.get()).get();
    }

    public float getColorSurfaceLeft() {
        return ((EV3ColorSensor) surfaceLeft.get()).getRed();
    }

    public float getColorSurfaceRight() {
        return ((EV3ColorSensor) surfaceRight.get()).getRed();
    }

    public int getColorBoat() {
        return ((EV3ColorSensor) boat.get()).getColor();
    }
}
