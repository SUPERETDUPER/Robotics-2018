package utils;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import utils.Logger.LogTypes;

public class Config {

    public static final Mode currentMode = Mode.SIM;

    public static final Port PORT_MOTOR_LEFT = MotorPort.A;
    public static final Port PORT_MOTOR_RIGHT = MotorPort.B;

    public static final Port PORT_SENSOR_COLOR_SURFACE = SensorPort.S3;
    public static final Port PORT_SENSOR_ULTRASONIC = SensorPort.S2;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";

    public static final float GUI_DISPLAY_RATIO = 8;
    static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;
    public static final String LOCAL_IP_ADDRESS = "192.168.0.14";

    public enum Mode {
        STANDALONE,
        DUAL,
        SIM
    }
}
