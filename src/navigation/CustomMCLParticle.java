package navigation;

import PC.MapGUI;
import geometry.SurfaceMap;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

import java.awt.*;
import java.util.Random;

/**
 * Inspired by Lawrie Griffiths' MCLParticle class in LEJOS EV3 Source code
 */
public class CustomMCLParticle {
    //private static final String LOG_TAG = CustomMCLParticle.class.getSimpleName();

    private static final float DISPLAY_TAIL_LENGTH = 0.3F;
    private static final float DISPLAY_ANGLE_WIDTH = 10;

    private static final Random rand = new Random();

    private Pose pose;
    private float weight = 1;

    /**
     * Create a particle with a specific pose
     *
     * @param pose the pose
     */
    CustomMCLParticle(Pose pose) {
        this.pose = pose;
    }

    /**
     * Return the weight of this particle
     *
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Set the weight for this particle
     *
     * @param weight the weight of this particle
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Return the pose of this particle
     *
     * @return the pose
     */
    public Pose getPose() {
        return pose;
    }

    /**
     * Calculate the weight for this particle by comparing its readings with the
     * robot's readings
     */
    public void calculateWeight(int color) {
        if (!SurfaceMap.contains(pose.getLocation()) || SurfaceMap.colorAtPoint(pose.getLocation()) != color) {
            weight = 0;
            return;
        }
        weight = 1;
    }

    /**
     * Apply the robot's move to the particle with a bit of random noise.
     * Only works for rotate or travel movements.
     *
     * @param move the robot's move
     */
    public void applyMove(Move move, float distanceNoiseFactor, float angleNoiseFactor) {
        float ym = (move.getDistanceTraveled() * ((float) Math.sin(Math.toRadians(pose.getHeading()))));
        float xm = (move.getDistanceTraveled() * ((float) Math.cos(Math.toRadians(pose.getHeading()))));

        pose.setLocation(new Point((float) (pose.getX() + xm + (distanceNoiseFactor * xm * rand.nextGaussian())),
                (float) (pose.getY() + ym + (distanceNoiseFactor * ym * rand.nextGaussian()))));
        pose.setHeading((float) (pose.getHeading() + move.getAngleTurned()
                + (angleNoiseFactor * rand.nextGaussian())));
        pose.setHeading((float) ((int) (pose.getHeading() + 0.5f) % 360));
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.BLUE);

        Point leftEnd = pose.pointAt(MapGUI.adjustSize(DISPLAY_TAIL_LENGTH), pose.getHeading() - DISPLAY_ANGLE_WIDTH);
        Point rightEnd = pose.pointAt(MapGUI.adjustSize(DISPLAY_TAIL_LENGTH), pose.getHeading() + DISPLAY_ANGLE_WIDTH);

        int[] xValues = new int[]{
                MapGUI.adjustSize(pose.getX()),
                MapGUI.adjustSize(leftEnd.x),
                MapGUI.adjustSize(rightEnd.x)
        };

        int[] yValues = new int[]{
                MapGUI.adjustSize(pose.getY()),
                MapGUI.adjustSize(leftEnd.y),
                MapGUI.adjustSize(rightEnd.y)
        };

        g.fillPolygon(xValues, yValues, xValues.length);
    }
}