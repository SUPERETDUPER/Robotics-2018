package EV3;

import Common.navigation.CustomPath;
import Common.navigation.MCL.CustomPoseProvider;
import Common.utils.Logger;
import EV3.hardware.ChassisBuilder;
import lejos.robotics.navigation.*;

public class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();


    private static final double ANGULAR_ACCELERATION = 1200;
    private static final double LINEAR_ACCELERATION = 400;

    //Set actual values
    private static final Pose STARTING_POSE = new Pose(500, 100, 0);

    private final MovePilot pilot;
    private final CustomPoseProvider poseProvider;
    //private final Navigator navigator;

    Controller() {
        pilot = new MovePilot(ChassisBuilder.get());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.addMoveListener(this);

        poseProvider = new CustomPoseProvider(pilot);
        poseProvider.setPose(STARTING_POSE);

        //navigator = new Navigator(pilot, poseProvider);

        //navigator.addNavigationListener(this);
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move started " + move.toString());
        CustomPath path = new CustomPath();
        Pose currentLocation = poseProvider.getPose();
        path.add(new Waypoint(currentLocation));
        path.add(new Waypoint(currentLocation.getLocation().pointAt(move.getDistanceTraveled(), currentLocation.getHeading() + move.getAngleTurned())));
        DataSender.sendPath(path);
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move stopped " + move.toString());
    }

    @Override
    public void pathComplete(Waypoint waypoint, Pose pose, int i) {

    }

    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {

    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {

    }

    void goTo(float x, float y) {
        //navigator.goTo(x, y);
    }

    void waitForCompletion() {
        //navigator.waitForStop();
    }

    void travel() {
        pilot.travel(100, false);
    }
}