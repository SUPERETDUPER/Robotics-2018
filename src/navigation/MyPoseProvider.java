package navigation;

import PC.Connection;
import PC.Displayable;
import PC.GUI;
import com.sun.istack.internal.NotNull;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import utils.Config;
import utils.Logger;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Inspired by Lawrie Griffiths' and Roger Glassey's MCLPoseProvider class in EV3 Lejos Source Code
 */

public class MyPoseProvider implements PoseProvider, MoveListener, Transmittable, Displayable {

    private static final String LOG_TAG = MyPoseProvider.class.getSimpleName();

    private static final MyPoseProvider mMCLPoseProvider = new MyPoseProvider();

    private static final float GUI_TAIL_LENGTH = 1;
    private static final float GUI_ANGLE_WIDTH = 10;

    private final ParticleSet particleSet = new ParticleSet();
    private Pose currentPose;
    private boolean updated = false;


    private MyPoseProvider() {
    }

    @NotNull
    public static MyPoseProvider get() {
        return mMCLPoseProvider;
    }

    public void attachMoveProvider(@NotNull MoveProvider mp) {
        mp.addMoveListener(this);
    }

    public void moveStarted(@NotNull Move event, @NotNull MoveProvider mp) {
        updated = false;
    }

    public void moveStopped(@NotNull Move event, @NotNull MoveProvider mp) {
        particleSet.applyMove(event);
        update(false);
        updated = true;
    }

    public void update(@NotNull boolean onEdge) {
        if (updated) {
            particleSet.calculateWeights(new Readings(onEdge));
            particleSet.resample();
        }

        estimatePose();

        if (Config.USING_PC) {
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
        update(false);

        return currentPose;
    }

    /**
     * set the initial currentPose cloud with radius noise 1 and heading noise 1
     */
    public void setPose(@NotNull Pose aPose) {
        this.currentPose = aPose;
        particleSet.setInitialPose(aPose);
        updated = true;
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

    public void displayOnGUI(@NotNull Graphics g) {
        particleSet.displayOnGUI(g);

        if (currentPose == null) {
            Logger.warning(LOG_TAG, "Could not paint robots location because it's null");
            return;
        }

        g.setColor(Color.RED);

        lejos.robotics.geometry.Point leftEnd = currentPose.pointAt(GUI.adjustSize(GUI_TAIL_LENGTH), currentPose.getHeading() - GUI_ANGLE_WIDTH);
        Point rightEnd = currentPose.pointAt(GUI.adjustSize(GUI_TAIL_LENGTH), currentPose.getHeading() + GUI_ANGLE_WIDTH);

        int[] xValues = new int[]{
                GUI.adjustSize(currentPose.getX()),
                GUI.adjustSize(leftEnd.x),
                GUI.adjustSize(rightEnd.x)
        };

        int[] yValues = new int[]{
                GUI.adjustSize(currentPose.getY()),
                GUI.adjustSize(leftEnd.y),
                GUI.adjustSize(rightEnd.y)
        };

        g.fillPolygon(xValues, yValues, 3);
    }

    public void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        if (currentPose == null) {
            dos.writeFloat(-1F);
        } else {
            dos.writeFloat(currentPose.getX());
            dos.writeFloat(currentPose.getY());
            dos.writeFloat(currentPose.getHeading());
        }

        particleSet.dumpObject(dos);
    }

    public void loadObject(@NotNull DataInputStream dis) throws IOException {
        float firstFloat = dis.readFloat();
        if (firstFloat != -1F) {
            this.currentPose = new Pose(firstFloat, dis.readFloat(), dis.readFloat());
        }

        particleSet.loadObject(dis);
    }
}
