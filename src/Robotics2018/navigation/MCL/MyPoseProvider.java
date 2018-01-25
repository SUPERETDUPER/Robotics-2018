package Robotics2018.navigation.MCL;

import Robotics2018.PC.Connection;
import com.sun.istack.internal.NotNull;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import Robotics2018.utils.Logger;

/**
 * Inspired by Lawrie Griffiths' and Roger Glassey's MCLPoseProvider class in EV3 Lejos Source Code
 */

public class MyPoseProvider extends PoseProviderContainer implements PoseProvider, MoveListener {

    private static final String LOG_TAG = MyPoseProvider.class.getSimpleName();

    private boolean updated = false;
    private boolean inMove;

    public MyPoseProvider(@NotNull MoveProvider mp) {
        mp.addMoveListener(this);
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        inMove = true;
        updated = false;
    }

    public void moveStopped(@NotNull Move event, @NotNull MoveProvider mp) {
        particleSet.applyMove(event);
        inMove = false;
    }

    public void update(Reading readings) {
        if (inMove) {
            Logger.error(LOG_TAG, "Can not update because robot is moving");
            return;
        }


        particleSet.calculateWeights(readings);
        particleSet.resample();

        estimatePose();
        updated = true;
        updateComputer();
    }

    private void updateComputer(){
        if (Connection.isConnected()) {
            Connection.EV3.sendMCLData();
        }
    }

    /**
     * Returns the best best estimate of the current currentPose;
     *
     * @return the estimated currentPose
     */
    @NotNull
    public Pose getPose() {
        if (!updated) {
            update(new SurfaceReading());
        }

        Logger.info(LOG_TAG, "Current pose is " + currentPose.toString());

        return currentPose;
    }

    /**
     * set the initial currentPose cloud with radius noise 1 and heading noise 1
     */
    public void setPose(@NotNull Pose aPose) {
        this.currentPose = aPose;
        updated = true;
        particleSet.setInitialPose(aPose);
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    private void estimatePose() {
        float totalWeights = 0;
        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;

        for (Particle particle : particleSet) {
            Pose p = particle.getPose();

            float x = p.getX();
            float y = p.getY();

            float weight = particle.getWeight();
            //float weight = 1; // weight is historic at this point, as resample has been done
            estimatedX += (x * weight);
            estimatedY += (y * weight);
            float head = p.getHeading();
            estimatedAngle += (head * weight);
            totalWeights += weight;

        }

        estimatedX /= totalWeights;
        estimatedY /= totalWeights;
        estimatedAngle /= totalWeights;

        // Normalize angle
        while (estimatedAngle > 180) estimatedAngle -= 360;
        while (estimatedAngle < -180) estimatedAngle += 360;

        currentPose = new Pose(estimatedX, estimatedY, estimatedAngle);
    }
}
