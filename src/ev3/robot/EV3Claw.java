/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

/**
 * Class responsible for moving the robots arm to different positions
 */
public class EV3Claw {
    private static final int ANGLE = 750;

    private final CustomEV3MediumMotor motor = new CustomEV3MediumMotor(Ports.MOTOR_CLAW);

    void setup(){
        motor.setup();
    }

    boolean stillSettingUp(){
        return motor.stillSettingUp();
    }

    public void drop(boolean immediateReturn) {
        motor.get().setSpeed((int) motor.get().getMaxSpeed());
        motor.get().rotate(-ANGLE, immediateReturn);
    }

    public void raise(boolean immediateReturn) {
        motor.get().setSpeed((int) motor.get().getMaxSpeed());
        motor.get().rotate(ANGLE, immediateReturn);
    }

    public void waitComplete(){
        motor.get().waitComplete();
    }
}
