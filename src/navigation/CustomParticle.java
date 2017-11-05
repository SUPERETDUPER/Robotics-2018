// Modified version of the MCLParticle class from LeJOS EV3

package navigation;

import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

import java.util.Random;

public class CustomParticle {
    private static Random rand = new Random();
    private Pose pose;
    private float weight = 1.0F;

    public CustomParticle(Pose pose) {
        this.pose = pose;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Pose getPose() {
        return this.pose;
    }

    public void applyMove(Move move, float distanceNoiseFactor, float angleNoiseFactor) {
        float hypotenuse = move.getDistanceTraveled();
        float currentH = this.pose.getHeading();

        float dy = hypotenuse * (float) Math.sin(Math.toRadians(currentH));
        float dx = hypotenuse * (float) Math.cos(Math.toRadians(currentH));
        float dh = move.getAngleTurned();

        float newX = this.pose.getX() + dx + distanceNoiseFactor * dx * (float) rand.nextGaussian();
        float newY = this.pose.getY() + dy + distanceNoiseFactor * dy * (float) rand.nextGaussian();

        float newH = (currentH + dh + angleNoiseFactor * dh * (float) rand.nextGaussian()) % 360F;

        this.pose.setLocation(newX, newY);
        this.pose.setHeading(newH);
    }

    public void reCalculateWeight(RangeReadings rr, RangeMap map, float divisor) {
        this.weight = 1.0F;
        Pose testPose = new Pose();
        testPose.setLocation(this.pose.getLocation());

        if (!map.inside(this.pose.getLocation())) {
            this.weight = 0.0F;
            return;
        }

        for (RangeReading rangeReading : rr) {
            testPose.setHeading(this.pose.getHeading() + rangeReading.getAngle());
            float theoreticalReading = map.range(testPose);

            if (theoreticalReading < 0.0F) {
                this.weight = 0.0F;
                return;
            }

            float robotReading = rangeReading.getRange();

            float diff = robotReading - theoreticalReading;
            this.weight /= (float) Math.exp(diff * diff / divisor);
        }
    }
}