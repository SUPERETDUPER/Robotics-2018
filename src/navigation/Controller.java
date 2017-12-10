package navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import utils.Config;

public class Controller {

    private static final String LOG_TAG = Controller.class.getSimpleName();

    private static final double WHEEL_DIAMETER = 5.59;
    private static final double WHEEL_OFFSET = 8.24;

    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 40;

    //Set actual values
    private static final Pose STARTING_POSE = new Pose(0, 0, 0);


    private static final Navigator navigator;

    static {
        MovePilot pilot = createMovePilot();
        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);


        CustomMCLPoseProvider.get().attachMoveProvider(pilot);

        navigator = new Navigator(pilot, CustomMCLPoseProvider.get());

        /*navigator.addNavigationListener(new NavigationListener() {
            @Override
            public void atWaypoint(Waypoint waypoint, Pose pose, int i) {

            }

            @Override
            public void pathComplete(Waypoint waypoint, Pose pose, int i) {

            }

            @Override
            public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {

            }
        });*/
    }

    public static void init() {

    }

    private static MovePilot createMovePilot() {
        RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_LEFT);
        RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_RIGHT);

        Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);
        Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_OFFSET);

        WheeledChassis chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

        return new MovePilot(chassis);
    }

    public static void test() {
        Path myPath = new Path();
        myPath.add(new Waypoint(20, 20));
        myPath.add(new Waypoint(40, -30));
        myPath.add(new Waypoint(-50, 30));
        myPath.add(new Waypoint(0, 0, 90));
        //navigator.setPath(myPath);
        //navigator.followPath();
        //navigator.waitForStop();
    }
}
