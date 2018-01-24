package Robotics2018.navigation;

import com.sun.istack.internal.NotNull;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.*;
import Robotics2018.navigation.MCL.MyPoseProvider;
import Robotics2018.sim.AbstractMotor;
import Robotics2018.Config;
import Robotics2018.utils.Logger;

public class Controller implements MoveListener{

    private static final String LOG_TAG = Controller.class.getSimpleName();

    private static final double WHEEL_DIAMETER = 5.59;
    private static final double WHEEL_OFFSET = 8.24;

    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 40;

    //Set actual values
    private static final Pose STARTING_POSE = new Pose(0, 0, 0);

    private static final Controller mController = new Controller();

    private final MovePilot pilot;
    //private static final Navigator navigator;

    private Controller(){
        pilot = createMovePilot();

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.addMoveListener(this);

        MyPoseProvider.get().attachMoveProvider(pilot);
        MyPoseProvider.get().setPose(STARTING_POSE);
    }

    public void travel(int distance) {
        pilot.travel(distance);
    }

    public static Controller get() {
        return mController;
    }

    public static void init(){

    }

    @NotNull
    private MovePilot createMovePilot() {
        RegulatedMotor leftMotor;
        RegulatedMotor rightMotor;

        if (Config.isSimulator) {
            leftMotor = new AbstractMotor();
            rightMotor = new AbstractMotor();
        } else {
            leftMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_LEFT);
            rightMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_RIGHT);
        }

        Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);
        Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_OFFSET);

        WheeledChassis chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

        return new MovePilot(chassis);
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move started " + move.toString());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move stopped " + move.toString());
    }
}