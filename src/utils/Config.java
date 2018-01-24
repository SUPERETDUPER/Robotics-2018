package utils;

import PC.Connection;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import utils.Logger.LogTypes;

public class Config {

    public enum Mode {
        STANDALONE,
        DUAL,
        SIM
    }

    public static final Mode currentMode = Mode.SIM;

    public static final float GUI_DISPLAY_RATIO = 8;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static final Port PORT_MOTOR_LEFT;
    public static final Port PORT_MOTOR_RIGHT;
    public static final Port PORT_SENSOR_COLOR_SURFACE;
    public static final Port PORT_SENSOR_ULTRASONIC;
    static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;

    static {
        if (Connection.runningOn == Connection.RUNNING_ON.EV3) {
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
