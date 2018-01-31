package EV3.hardware;

import Common.Config;
import EV3.sim.AbstractMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class ChassisBuilder {

    private static final double WHEEL_DIAMETER = 55.9;
    private static final double WHEEL_OFFSET = 82.4;

    public static Chassis getChassis() {
        RegulatedMotor leftMotor;
        RegulatedMotor rightMotor;

        if (Config.useSimulator) {
            leftMotor = new AbstractMotor("Left motor");
            rightMotor = new AbstractMotor("Right motor");
        } else {
            leftMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT);
            rightMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT);
        }

        Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);
        Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_OFFSET);

        return new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
    }
}
