package Robotics2018;

import Robotics2018.PC.Connection;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import Robotics2018.utils.Logger.LogTypes;

public final class Config {

    public static final boolean isSimulator = true;
    public static final boolean isDual = true;

    public static final float GUI_DISPLAY_RATIO = 0.8F;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static final Port PORT_MOTOR_LEFT;
    public static final Port PORT_MOTOR_RIGHT;
    public static final Port PORT_SENSOR_COLOR_SURFACE;
    public static final Port PORT_SENSOR_ULTRASONIC;
    public static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;

    static {
        if (Connection.isEV3 && !isSimulator) {
            PORT_MOTOR_LEFT = MotorPort.A;
            PORT_MOTOR_RIGHT = MotorPort.B;
            PORT_SENSOR_COLOR_SURFACE = SensorPort.S3;
            PORT_SENSOR_ULTRASONIC = SensorPort.S2;
        } else {
            PORT_MOTOR_LEFT = null;
            PORT_MOTOR_RIGHT = null;
            PORT_SENSOR_COLOR_SURFACE = null;
            PORT_SENSOR_ULTRASONIC = null;
        }
    }
}
