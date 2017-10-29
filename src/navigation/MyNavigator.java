package navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.*;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.EV3NavigationModel;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;
import utils.Config;
import utils.Logger;

public class MyNavigator {

    private static final String LOG_TAG = MyNavigator.class.getSimpleName();

    //TODO : Set actual values
    private static final double WHEEL_DIAMETER = 0;
    private static final double WHEEL_OFFSET = 0;

    //TODO : Test and understand values
    private static final int NUMBER_OF_PARTICLES = 200;
    private static final int MAP_BORDER = 10;
    private static final float INITIAL_RADIUS_NOISE = 1;
    private static final float INITIAL_HEADING_NOISE = 1;

    //TODO : Map actual surface
    private static final Rectangle BOUNDING_RECTANGLE = new Rectangle(0, 0, 0, 0);
    private static final Line[] LINES = new Line[]{
            new Line(0, 0, 0, 0),
            new Line(0, 0, 0, 0)
    };
    private static final Pose STARTING_POSE = new Pose(0, 0, 0);


    private static final LineMap map = new LineMap(LINES, BOUNDING_RECTANGLE);
    private static final ShortestPathFinder pathFinder = new ShortestPathFinder(map);

    private static MCLPoseProvider poseProvider;
    private static Navigator navigator;
    private static EV3NavigationModel ev3Model;

    static {
        MovePilot pilot = createMovePilot();

        EV3UltrasonicSensor distanceSensor = new EV3UltrasonicSensor(Config.PORT_ULTRASONIC_SENSOR);
        RangeFinder finder = new RangeFinderAdapter(distanceSensor.getDistanceMode());

        RangeScanner scanner = new FixedRangeScanner(pilot, finder);

        poseProvider = new MCLPoseProvider(pilot, scanner, map, NUMBER_OF_PARTICLES, MAP_BORDER);
        poseProvider.setInitialPose(STARTING_POSE, INITIAL_RADIUS_NOISE, INITIAL_HEADING_NOISE);

        navigator = new Navigator(pilot, poseProvider);

        ev3Model = new EV3NavigationModel();
        ev3Model.addNavigator(navigator);
        ev3Model.addPoseProvider(poseProvider);
        ev3Model.addPilot(pilot);
        ev3Model.addRangeScanner(scanner);
    }

    private static MovePilot createMovePilot() {
        RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_LEFT);
        RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(Config.PORT_MOTOR_RIGHT);

        Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);
        Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET);

        WheeledChassis chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

        return new MovePilot(chassis);
    }

    public static void goToDestination(Waypoint destination, boolean immediateReturn) {
        try {
            Path path = pathFinder.findRoute(poseProvider.getPose(), destination);

            navigator.followPath(path);

        } catch (DestinationUnreachableException e) {
            Logger.log(Logger.typeError, LOG_TAG, "Destination unreachable");
        }
        if (!immediateReturn) {
            navigator.waitForStop();
        }
    }
}
