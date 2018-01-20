package navigation;

import PC.Connection;
import PC.MapGUI;
import com.sun.istack.internal.NotNull;
import hardware.ColorSensor;
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
 * Inspired by Lawrie Griffiths' and Roger Glassey's code
 */

public class CustomMCLPoseProvider implements PoseProvider, MoveListener, Transmittable {

    private static final String LOG_TAG = CustomMCLPoseProvider.class.getSimpleName();

    private static final CustomMCLPoseProvider mMCLPoseProvider = new CustomMCLPoseProvider();

    private static final float GUI_TAIL_LENGTH = 1;
    private static final float GUI_ANGLE_WIDTH = 10;

    private CustomMCLParticleSet particleSet = new CustomMCLParticleSet();
    private Pose currentPose;
    private boolean updated = false;


    private CustomMCLPoseProvider() {
    }

    public static CustomMCLPoseProvider get() {
        return mMCLPoseProvider;
    }

    public void attachMoveProvider(@NotNull MoveProvider mp) {
        mp.addMoveListener(this);
    }

    public void setStartingPose(Pose aPose, float radiusNoise, float headingNoise) {
        this.currentPose = aPose;
        particleSet.setInitialPose(aPose, radiusNoise, headingNoise);
        updated = true;
    }


    public void moveStarted(Move event, MoveProvider mp) {
        updated = false;
    }

    public void moveStopped(Move event, MoveProvider mp) {
        particleSet.applyMove(event);
    }


    private void update() {
        update(ColorSensor.getSurfaceColor());
    }


    private void update(int color) {
        updated = false;

        particleSet.calculateWeights(color);


        particleSet.resample();
        updated = true;
    }

    /**
     * Returns the best best estimate of the current currentPose;
     *
     * @return the estimated currentPose
     */
    public Pose getPose() {
        if (!updated) {
            update();
        }

        estimatePose();

        if (Config.USING_PC) {
            Connection.EV3.sendMCLData();
        }

        return currentPose;
    }

    /**
     * set the initial currentPose cloud with radius noise 1 and heading noise 1
     */
    public void setPose(Pose aPose) {
        setStartingPose(aPose, 1, 1);
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

        for (int i = 0; i < particleSet.getSize(); i++) {
            Pose p = particleSet.getParticle(i).getPose();
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
        if (particleSet != null) {
            particleSet.paintComponent(g);
        }

        if (currentPose == null) {
            Logger.warning(LOG_TAG, "Could not paint robots location because it's null");
            return;
        }

        g.setColor(Color.RED);

        lejos.robotics.geometry.Point leftEnd = currentPose.pointAt(MapGUI.adjustSize(GUI_TAIL_LENGTH), currentPose.getHeading() - GUI_ANGLE_WIDTH);
        Point rightEnd = currentPose.pointAt(MapGUI.adjustSize(GUI_TAIL_LENGTH), currentPose.getHeading() + GUI_ANGLE_WIDTH);

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
        if (currentPose == null) {
            dos.writeFloat(-1F);
        } else {
            dos.writeFloat(currentPose.getX());
            dos.writeFloat(currentPose.getY());
            dos.writeFloat(currentPose.getHeading());
        }

        particleSet.dumpObject(dos);
    }

    public void loadObject(DataInputStream dis) throws IOException {
        float firstFloat = dis.readFloat();
        if (firstFloat != -1F) {
            this.currentPose = new Pose(firstFloat, dis.readFloat(), dis.readFloat());
        }

        particleSet.loadObject(dis);
    }
}
