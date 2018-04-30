/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

/**
 * Defines the Ports for the sensors and motors
 */
final class Ports {
    static final Port MOTOR_LEFT = MotorPort.A;
    static final Port MOTOR_RIGHT = MotorPort.B;
    static final Port MOTOR_CLAW = MotorPort.C;
    static final Port MOTOR_ARM = MotorPort.D;
    static final Port SENSOR_COLOR_SURFACE_LEFT = SensorPort.S1;
    static final Port SENSOR_COLOR_SURFACE_RIGHT = SensorPort.S2;
    static final Port SENSOR_COLOR_BOAT = SensorPort.S3;
    static final Port TOUCH = SensorPort.S4;
}
