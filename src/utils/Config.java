package utils;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

public class Config {
    public static final Port PORT_MOTOR_LEFT = MotorPort.A;
    public static final Port PORT_MOTOR_RIGHT = MotorPort.B;

    public static final Port PORT_SENSOR_COLOR_SURFACE = SensorPort.S3;
    public static final Port PORT_SENSOR_ULTRASONIC = SensorPort.S2;

}
