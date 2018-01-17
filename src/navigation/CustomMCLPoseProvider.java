package navigation;

import PC.MapGUI;
import hardware.ColorSensor;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import utils.logger.Logger;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Maintains an estimate of the robot currentPose using sensor data.  It uses Monte Carlo
 * Localization  (See section 8.3 of "Probabilistic Robotics" by Thrun et al. <br>
 * Uses a {@link CustomMCLParticleSet} to represent the probability distribution  of the
 * estimated currentPose.
 * It uses a {@link MoveProvider} to supply odometry
 * data whenever  a movement is completed,
 * from which the {@link Pose} of each particle is updated.
 * It then uses a to provide
 * which are used, together with the
 * {@link RangeMap} to calculate the
 * probability weight of  each {@link CustomMCLParticle} .
 *
 * @author Lawrie Griffiths and Roger Glassey
 */

public class CustomMCLPoseProvider implements PoseProvider, MoveListener, Transmittable {

    private static final String LOG_TAG = CustomMCLPoseProvider.class.getSimpleName();

    private static final CustomMCLPoseProvider mMCLPoseProvider = new CustomMCLPoseProvider();
    private static final float TAIL_LENGTH = 1;
    private static final float ANGLE_WIDTH = 10;
    //private boolean debug = false;
    private Pose currentPose;
    private boolean updated = false;


    private CustomMCLPoseProvider() {
    }

    public static CustomMCLPoseProvider get() {
        return mMCLPoseProvider;
    }

    public void attachMoveProvider(MoveProvider mp) {
        if (mp != null) mp.addMoveListener(this);
    }

    /**
     * Generates an  initial particle set in a circular normal distribution, centered
     * on aPose.
     *
     * @param aPose        - center of the cloud
     * @param radiusNoise  - standard deviation of the radius of the cloud
     * @param headingNoise - standard deviation of the heading;
     */
    public void setInitialPose(Pose aPose, float radiusNoise, float headingNoise) {
        this.currentPose = aPose;
        CustomMCLParticleSet.get().setInitialPose(aPose, radiusNoise, headingNoise);
        updated = true;
    }

    /**
     * Required by MoveListener interface; does nothing
     */
    public void moveStarted(Move event, MoveProvider mp) {
        updated = false;
    }

    /**
     * Required by MoveListener interface. The currentPose of each particle is updated
     * using the odometry data of the Move object.
     *
     * @param event the move  just completed
     * @param mp    the MoveProvider
     */
    public void moveStopped(Move event, MoveProvider mp) {
        //if (debug) System.out.println("CustomMCL move stopped");
        //updated = false;
        //updater.moveStopped(event);
        CustomMCLParticleSet.get().applyMove(event);
    }

    /**
     * Calls range scanner to get range readings, calculates the probabilities
     * of each particle from the range  readings and the map and calls resample(()
     *
     * @return true if update was successful
     */
    public boolean update() {
        return update(ColorSensor.getSurfaceColor());
    }

    /**
     * Calculates particle weights from readings, then resamples the particle set;
     *
     * @return true if update was successful.
     */
    public boolean update(int color) {
        //if (debug) System.out.println("CustomMCLPP update readings called ");
        updated = false;

        CustomMCLParticleSet.get().calculateWeights(color);

        //if (debug) System.out.println(" max Weight " + particles.getMaxWeight() +
        //        " Good currentPose " + goodPose);

        //if (!goodPose) {
        //  if (debug) System.out.println("Sensor data improbable from this currentPose ");
        //return false;
        //}

        boolean goodPose = CustomMCLParticleSet.get().resample();
        updated = goodPose;

        //if (debug) {
        //  if (goodPose) System.out.println("Resample done");
        //else System.out.println("Resample failed");
        //}

        return goodPose;
    }

    /**
     * Returns the best best estimate of the current currentPose;
     *
     * @return the estimated currentPose
     */
    public Pose getPose() {
        //if (debug) System.out.println("CustomMCL call update; updated? " + updated);

        if (!updated) {
            update();
        }

        estimatePose();
        return currentPose;
    }

    /**
     * set the initial currentPose cloud with radius noise 1 and heading noise 1
     */
    public void setPose(Pose aPose) {
        setInitialPose(aPose, 1, 1);
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    private void estimatePose() {
        final float BIG_FLOAT = 1000000f;
        float minX, maxX, minY, maxY;
        float totalWeights = 0;
        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;
        minX = BIG_FLOAT;
        minY = BIG_FLOAT;
        maxX = -BIG_FLOAT;
        maxY = -BIG_FLOAT;

        for (int i = 0; i < CustomMCLParticleSet.get().getSize(); i++) {
            Pose p = CustomMCLParticleSet.get().getParticle(i).getPose();
            float x = p.getX();
            float y = p.getY();
            //float weight = particles.getParticle(i).getWeight();
            float weight = 1; // weight is historic at this point, as resample has been done
            estimatedX += (x * weight);
            estimatedY += (y * weight);
            float head = p.getHeading();
            estimatedAngle += (head * weight);
            totalWeights += weight;

            if (x < minX) minX = x;

            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        estimatedX /= totalWeights;
        estimatedY /= totalWeights;
        estimatedAngle /= totalWeights;

        // Normalize angle
        while (estimatedAngle > 180) estimatedAngle -= 360;
        while (estimatedAngle < -180) estimatedAngle += 360;

        currentPose = new Pose(estimatedX, estimatedY, estimatedAngle);
    }

    public void paintComponent(Graphics g) {
        CustomMCLParticleSet.get().paintComponent(g);

        if (currentPose == null) {
            Logger.print(Logger.typeWarning, LOG_TAG, "Robot location currentPose is null");
            return;
        }

        g.setColor(Color.RED);

        lejos.robotics.geometry.Point leftEnd = currentPose.pointAt(MapGUI.adjustSize(TAIL_LENGTH), currentPose.getHeading() - ANGLE_WIDTH);
        Point rightEnd = currentPose.pointAt(MapGUI.adjustSize(TAIL_LENGTH), currentPose.getHeading() + ANGLE_WIDTH);

        int[] xValues = new int[]{
                MapGUI.adjustSize(currentPose.getX()),
                MapGUI.adjustSize(leftEnd.x),
                MapGUI.adjustSize(rightEnd.x)
        };

        int[] yValues = new int[]{
                MapGUI.adjustSize(currentPose.getY()),
                MapGUI.adjustSize(leftEnd.y),
                MapGUI.adjustSize(rightEnd.y)
        };

        g.fillPolygon(xValues, yValues, 3);
    }

    public void dumpObject(DataOutputStream dos) throws IOException {
        dos.writeFloat(currentPose.getX());
        dos.writeFloat(currentPose.getY());
        dos.writeFloat(currentPose.getHeading());
        dos.flush();
    }

    public void loadObject(DataInputStream dis) throws IOException {
        this.currentPose.setLocation(dis.readFloat(), dis.readFloat());
        this.currentPose.setHeading(dis.readFloat());
    }
}
