/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import org.jetbrains.annotations.NotNull;

public class Util {
    private static final double WHEEL_OFFSET = 63; //Real value is around 56 but testing shows higher is better
    private static final double WHEEL_DIAMETER = 80.5;

    @NotNull
    public static Chassis buildChassis(RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
        Wheel[] wheels = new Wheel[]{
                WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET),
                WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_OFFSET)
        };

        return new WheeledChassis(wheels, WheeledChassis.TYPE_DIFFERENTIAL);
    }
}
