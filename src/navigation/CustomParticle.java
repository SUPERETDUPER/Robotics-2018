//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package navigation;

import java.util.Random;
import lejos.robotics.RangeReadings;
import lejos.robotics.geometry.Point;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

public class CustomParticle {
    private static Random rand = new Random();
    private Pose pose;
    private float weight = 1.0F;
    private static boolean debug = false;

    public static void setDebug(boolean yes) {
        debug = yes;
    }

    public CustomParticle(Pose pose) {
        this.pose = pose;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return this.weight;
    }

    public Pose getPose() {
        return this.pose;
    }

    public void calculateWeight(float divisor) {
        /*
        this.weight = 1.0F;
        Pose tempPose = new Pose();
        tempPose.setLocation(this.pose.getLocation());

        // TODO : Fix method

        for(int i = 0; i < rr.getNumReadings(); ++i) {
            if (!map.inside(tempPose.getLocation())) {
                this.weight = 0.0F;
                return;
            }

            float angle = rr.getAngle(i);
            tempPose.setHeading(this.pose.getHeading() + angle);
            float robotReading = rr.getRange(i);
            float range = map.range(tempPose);
            if (range < 0.0F) {
                this.weight = 0.0F;
                if (debug) {
                    System.out.println("zero wt" + tempPose);
                }

                return;
            }

            float diff = robotReading - range;
            this.weight *= (float)Math.exp((double)(-(diff * diff) / divisor));
        }*/
    }

    public float getReading(int i, RangeReadings rr, RangeMap map) {
        Pose tempPose = new Pose();
        tempPose.setLocation(this.pose.getLocation());
        tempPose.setHeading(this.pose.getHeading() + rr.getAngle(i));
        return map.range(tempPose);
    }

    public RangeReadings getReadings(RangeReadings rr, RangeMap map) {
        RangeReadings pr = new RangeReadings(rr.getNumReadings());

        for(int i = 0; i < rr.getNumReadings(); ++i) {
            pr.setRange(i, rr.getAngle(i), this.getReading(i, rr, map));
        }

        return pr;
    }

    public void applyMove(Move move, float distanceNoiseFactor, float angleNoiseFactor) {
        float ym = move.getDistanceTraveled() * (float)Math.sin(Math.toRadians((double)this.pose.getHeading()));
        float xm = move.getDistanceTraveled() * (float)Math.cos(Math.toRadians((double)this.pose.getHeading()));
        this.pose.setLocation(new Point((float)((double)(this.pose.getX() + xm) + (double)(distanceNoiseFactor * xm) * rand.nextGaussian()), (float)((double)(this.pose.getY() + ym) + (double)(distanceNoiseFactor * ym) * rand.nextGaussian())));
        this.pose.setHeading((float)((double)(this.pose.getHeading() + move.getAngleTurned()) + (double)angleNoiseFactor * rand.nextGaussian()));
        this.pose.setHeading((float)((int)(this.pose.getHeading() + 0.5F) % 360));
    }
}
