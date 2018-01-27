package EV3;

import Common.navigation.MCL.CustomPoseProvider;
import Common.utils.Logger;
import EV3.hardware.ChassisBuilder;
import lejos.robotics.navigation.*;

public class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();


    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;

    //Set actual values
    private static final Pose STARTING_POSE = new Pose(500, 100, 0);

    private final MovePilot pilot;
    private final CustomPoseProvider poseProvider;
    private final Navigator navigator;

    Controller() {
        pilot = new MovePilot(ChassisBuilder.get());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);

        poseProvider = new CustomPoseProvider(pilot, STARTING_POSE);

        navigator = new Navigator(pilot, poseProvider.getOdometryPoseProvider());
        navigator.addNavigationListener(this);
        navigator.singleStep(true);

        pilot.addMoveListener(this);


    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        //DataSender.sendPath(navigator.getPath());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
    }

    @Override
    public void pathComplete(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "Path complete");
    }

    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "pathInterrupted");
    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "At waypoint");
    }

    void goTo(float x, float y) {
        navigator.goTo(500, 100, -90);
        poseProvider.getPose();
    }

    void travel() {
        pilot.travel(1000, false);
        pilot.rotate(-90, false);
        pilot.travel(400, false);
    }
}