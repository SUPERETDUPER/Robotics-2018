package Robotics2018.navigation.MCL;

import Robotics2018.PC.Connection;
import Robotics2018.PC.GUI.Displayable;
import com.sun.istack.internal.NotNull;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import Robotics2018.navigation.CustomPath;
import Robotics2018.utils.Logger;

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

    private static final float GUI_TAIL_LENGTH = 3;
    private static final float GUI_ANGLE_WIDTH = 10;

    private final ParticleSet particleSet = new ParticleSet();
    private Pose currentPose;


    private MyPoseProvider() {
    }

    @NotNull
    public static MyPoseProvider get() {
        return mMCLPoseProvider;
    }

    public void attachMoveProvider(@NotNull MoveProvider mp) {
        mp.addMoveListener(this);
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
    }

    public void moveStopped(@NotNull Move event, @NotNull MoveProvider mp) {
        CustomPath path = new CustomPath();
        path.add(new Waypoint(currentPose));
        path.add(new Waypoint(currentPose.getLocation().pointAt(event.getDistanceTraveled(), currentPose.getHeading() + event.getAngleTurned())));
        Connection.EV3.sendPath(path);
        particleSet.applyMove(event);

        updateComputer();
        update(new SurfaceReading());
    }

    public void update(Reading readings) {
        particleSet.calculateWeights(readings);
        particleSet.resample();

        estimatePose();
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
        update(new SurfaceReading());

        Logger.info(LOG_TAG, "Current pose is " + currentPose.toString());

        return currentPose;
    }

    /**
     * set the initial currentPose cloud with radius noise 1 and heading noise 1
     */
    public void setPose(@NotNull Pose aPose) {
        this.currentPose = aPose;
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

    public void displayOnGUI(@NotNull Graphics g) {
        if (currentPose == null) {
            Logger.warning(LOG_TAG, "Could not paint robots location because it's null");
            return;
        }

        g.setColor(Color.RED);

        lejos.robotics.geometry.Point leftEnd = currentPose.pointAt((int) GUI_TAIL_LENGTH, currentPose.getHeading() + 180 - GUI_ANGLE_WIDTH);
        Point rightEnd = currentPose.pointAt((int) GUI_TAIL_LENGTH, currentPose.getHeading() + 180 + GUI_ANGLE_WIDTH);

        int[] xValues = new int[]{
                (int) currentPose.getX(),
                (int) leftEnd.x,
                (int) rightEnd.x
        };

        int[] yValues = new int[]{
                (int) currentPose.getY(),
                (int) leftEnd.y,
                (int) rightEnd.y
        };

        g.fillPolygon(xValues, yValues, 3);
    }

    public ParticleSet getParticleSet() {
        return particleSet;
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
        Logger.info(LOG_TAG, "Loading new MCL data");
        float firstFloat = dis.readFloat();
        if (firstFloat != -1F) {
            this.currentPose = new Pose(firstFloat, dis.readFloat(), dis.readFloat());
        }

        particleSet.loadObject(dis);
    }
}
