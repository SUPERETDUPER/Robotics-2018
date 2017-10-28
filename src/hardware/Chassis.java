package hardware;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import utils.Config;

public class Chassis {

    private static final WheeledChassis chassis;

    //TODO : Set actual values
    private static final double WHEEL_DIAMETER = 0;
    private static final double WHEEL_OFFSET = 0;

    static {
        RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_LEFT);
        RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_RIGHT);

        Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);
        Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);

        chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
    }
}
