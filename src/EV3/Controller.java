package EV3;

import Common.utils.Logger;
import EV3.hardware.ChassisBuilder;
import EV3.navigation.*;
import lejos.robotics.navigation.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();
    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;
    private static final Pose STARTING_POSE = new Pose(500, 100, 0);

    private static final Controller controller = new Controller();

    @NotNull
    private final CustomMCLPoseProvider poseProvider;
    @NotNull
    private final MyNavigator navigator;

    private Controller() {
        MyMovePilot pilot = new MyMovePilot(ChassisBuilder.getChassis());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * 0.8D);
        pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * 0.8D);
        pilot.addMoveListener(this);

        poseProvider = new CustomMCLPoseProvider(pilot, STARTING_POSE);

        navigator = new MyNavigator(pilot, poseProvider);
        navigator.addNavigationListener(this);
        navigator.singleStep(true);
    }

    @NotNull
    @Contract(pure = true)
    public static Controller get() {
        return controller;
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
        Logger.info(LOG_TAG, "At Waypoint");
    }

    void goTo() {
        navigator.addWaypoint(new Waypoint(600, 200));
        navigator.addWaypoint(new Waypoint(1200, 400));
        navigator.addWaypoint(new Waypoint(300, 1000));
        //navigator.addWaypoint(new Waypoint(305, 1000));
        navigator.followPath();
        LineChecker lineChecker = new LineChecker();
        while (navigator.isMoving()) {
            lineChecker.check();
            Thread.yield();
        }
        Logger.info(LOG_TAG, poseProvider.getPose().toString());
    }

    @NotNull
    public Pose getPose() {
        return poseProvider.getPose();
    }

    public void update(@NotNull Readings readings) {
        poseProvider.update(readings);
    }
}