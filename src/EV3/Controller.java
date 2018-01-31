package EV3;

import EV3.navigation.CustomMCLPoseProvider;
import Common.utils.Logger;
import EV3.hardware.ChassisBuilder;
import lejos.robotics.navigation.*;
import lejos.utility.Delay;

public class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();


    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;
    private static final Pose STARTING_POSE = new Pose(500, 100, 0);

    private final CustomMCLPoseProvider poseProvider;
    private final Navigator navigator;

    Controller() {
        MovePilot pilot = new MovePilot(ChassisBuilder.getChassis());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.addMoveListener(this);

        poseProvider = new CustomMCLPoseProvider(pilot, STARTING_POSE);

        navigator = new Navigator(pilot, poseProvider);
        navigator.addNavigationListener(this);
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        DataSender.sendPath(navigator.getPath());
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

    void goTo() {
        navigator.addWaypoint(600, 200);
        navigator.addWaypoint(1200, 400);
        navigator.addWaypoint(300, 1000);
        navigator.followPath();
        while (navigator.isMoving()) {
            poseProvider.getPose();
            Delay.msDelay(500);
        }
        Logger.info(LOG_TAG, poseProvider.getPose().toString());
    }
}