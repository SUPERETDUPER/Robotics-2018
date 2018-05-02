/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

/**
 * Class responsible for moving the robots arm to different positions
 */
public class EV3Arm {
    //TODO Test angle to be correct
    private static final int ANGLE = 10;
    private static final int SPEED = 200;

    private final CustomEV3MediumMotor motor = new CustomEV3MediumMotor(Ports.MOTOR_ARM);

    void setup(){
        motor.setup();
    }

    boolean stillSettingUp(){
        return motor.stillSettingUp();
    }

    public void drop() {
        motor.get().setSpeed(SPEED);
        motor.get().rotate(ANGLE);
    }

    public void raise() {
        motor.get().setSpeed(SPEED);
        motor.get().rotate(-ANGLE);
    }
}
