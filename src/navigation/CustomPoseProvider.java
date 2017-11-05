package navigation;

import geometry.SurfaceMap;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;

public class CustomPoseProvider implements PoseProvider, MoveListener {
    private static final int NUMBER_OF_PARTICLES = 200;
    private static final float INITIAL_RADIUS_NOISE = 1;
    private static final float INITIAL_HEADING_NOISE = 1;

    private Pose currentPose;
    private MoveProvider pilot;
    private CustomParticleSet particleSet;
    private boolean updated;
    private SurfaceMap map;


    CustomPoseProvider(MoveProvider pilot, SurfaceMap map, Pose initialPose) {
        particleSet = new CustomParticleSet();
        updated = true;

        this.map = map;
        this.pilot = pilot;
        this.pilot.addMoveListener(this);
    }

    @Override
    public Pose getPose() {
        if (!updated) {
            update();
        }
        return currentPose;
    }

    @Override
    public void setPose(Pose pose) {
        this.currentPose = pose;
        updated = true;
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        updated = false;
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
        particleSet.applyMove(move);
    }

    private void update() {

    }
}