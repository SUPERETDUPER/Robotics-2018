package utils;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

public class Config {
    //TODO : Confirm/assign real values
    public static final Port PORT_MOTOR_LEFT = MotorPort.A;
    public static final Port PORT_MOTOR_RIGHT = MotorPort.B;

    public static final Port PORT_ULTRASONIC_SENSOR = SensorPort.S1;
}
