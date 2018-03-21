/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.hardware;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

/**
 * Defines the Ports for the sensors and motors
 */
final class Ports {
    static final Port PORT_MOTOR_LEFT = MotorPort.A;
    static final Port PORT_MOTOR_RIGHT = MotorPort.B;
    static final Port PORT_SENSOR_COLOR_SURFACE = SensorPort.S3;
}
