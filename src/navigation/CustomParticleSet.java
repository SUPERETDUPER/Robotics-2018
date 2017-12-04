//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package navigation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import geometry.SurfaceMap;
import lejos.robotics.RangeReadings;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

public class CustomParticleSet implements Transmittable {
    private static final float BIG_FLOAT = 10000.0F;
    public static int maxIterations = 1000;
    private float twoSigmaSquared = 400.0F;
    private float distanceNoiseFactor = 0.2F;
    private float angleNoiseFactor = 4.0F;
    private int numParticles;
    private CustomParticle[] particles;
    private float maxWeight;
    private float totalWeight;
    private int border = 10;
    private Random random = new Random();
    private Rectangle boundingRect;
    private static boolean debug = false;
    private int _iterations;

    public CustomParticleSet(int numParticles, int border) {
        this.numParticles = numParticles;
        this.border = border;
        // TODO : get Bound rectangle
        //this.boundingRect = map.getBoundingRect();
        this.particles = new CustomParticle[numParticles];

        for (int i = 0; i < numParticles; ++i) {
            this.particles[i] = this.generateParticle();
        }

    }

    public CustomParticleSet(int numParticles, int border, float divisor, float minWeight) {
        if (debug) {
            System.out.println("New  Particles from readings");
        }

        int k = 1;
        this.numParticles = numParticles;
        this.border = border;
        // TODO : get Bound rectangle
        //this.boundingRect = map.getBoundingRect();
        this.particles = new CustomParticle[numParticles];
        int i = 0;

        while (i < numParticles) {
            ++k;
            CustomParticle particle = this.generateParticle();
            particle.calculateWeight(divisor);
            if (minWeight < particle.getWeight()) {
                this.particles[i] = particle;
                ++i;
                if (debug) {
                    System.out.println("generated " + i);
                }
            }
        }

        System.out.println("Total particles tried " + k);
    }

    public CustomParticleSet(int numParticles, Pose initialPose, float radiusNoise, float headingNoise) {
        this.numParticles = numParticles;
        this.border = 0;
        //TODO : Get bouding rectangle
        this.particles = new CustomParticle[numParticles];

        for (int i = 0; i < numParticles; ++i) {
            float rad = radiusNoise * (float) this.random.nextGaussian();
            float theta = (float) (6.283185307179586D * Math.random());
            float x = initialPose.getX() + rad * (float) Math.cos((double) theta);
            float y = initialPose.getY() + rad * (float) Math.sin((double) theta);
            float heading = initialPose.getHeading() + headingNoise * (float) this.random.nextGaussian();
            this.particles[i] = new CustomParticle(new Pose(x, y, heading));
            if (debug) {
                System.out.println(" new particle set ");
            }
        }

    }

    private CustomParticle generateParticle() {
        Rectangle innerRect = new Rectangle(this.boundingRect.x + (float) this.border, this.boundingRect.y + (float) this.border, this.boundingRect.width - (float) (this.border * 2), this.boundingRect.height - (float) (this.border * 2));

        float x;
        float y;
        do {
            x = innerRect.x + (float) Math.random() * innerRect.width;
            y = innerRect.y + (float) Math.random() * innerRect.height;
        } while (!SurfaceMap.getSurfaceMap().contains(new Point(x, y)));

        float angle = (float) Math.random() * 360.0F;
        return new CustomParticle(new Pose(x, y, angle));
    }

    public int numParticles() {
        return this.numParticles;
    }

    public static void setDebug(boolean debug) {
        debug = debug;
        if (debug) {
            System.out.println("ParticleSet Debug ON ");
        }

    }

    public CustomParticle getParticle(int i) {
        return this.particles[i];
    }

    public boolean resample() {
        CustomParticle[] oldParticles = this.particles;
        this.particles = new CustomParticle[this.numParticles];
        int count = 0;
        int iterations = 0;

        while (count < this.numParticles) {
            ++iterations;
            if (iterations >= maxIterations) {
                if (debug) {
                    System.out.println("Lost: count = " + count);
                }

                int i;
                if (count > 0) {
                    for (i = count; i < this.numParticles; ++i) {
                        this.particles[i] = new CustomParticle(this.particles[i % count].getPose());
                        this.particles[i].setWeight(this.particles[i % count].getWeight());
                    }

                    return false;
                }

                for (i = 0; i < this.numParticles; ++i) {
                    this.particles[i] = this.generateParticle();
                }

                return true;
            }

            float rand = (float) Math.random();

            for (int i = 0; i < this.numParticles && count < this.numParticles; ++i) {
                if (oldParticles[i].getWeight() >= rand) {
                    Pose p = oldParticles[i].getPose();
                    float x = p.getX();
                    float y = p.getY();
                    float angle = p.getHeading();
                    this.particles[count] = new CustomParticle(new Pose(x, y, angle));
                    this.particles[count++].setWeight(oldParticles[i].getWeight());
                }
            }
        }

        return true;
    }

    public boolean calculateWeights() {

        int zeros = 0;
        this.maxWeight = 0.0F;

        for (int i = 0; i < this.numParticles; ++i) {
            this.particles[i].calculateWeight(this.twoSigmaSquared);
            float weight = this.particles[i].getWeight();
            if (weight > this.maxWeight) {
                this.maxWeight = weight;
            }

            if (weight == 0.0F) {
                ++zeros;
            }
        }

        if (debug) {
            System.out.println("Calc Weights Max wt " + this.maxWeight + " Zeros " + zeros);
        }

        return (double) this.maxWeight >= 0.01D;
    }

    public void applyMove(Move move) {
        if (move == null) {
            System.out.println("applyMove: null move");
        } else {
            if (debug) {
                System.out.println("particles applyMove " + move.getMoveType());
            }

            this.maxWeight = 0.0F;

            for (int i = 0; i < this.numParticles; ++i) {
                this.particles[i].applyMove(move, this.distanceNoiseFactor, this.angleNoiseFactor);
            }

            if (debug) {
                System.out.println("particles applyMove Exit");
            }

        }
    }

    public float getMaxWeight() {
        float wt = 0.0F;

        for (int i = 0; i < this.particles.length; ++i) {
            wt = Math.max(wt, this.particles[i].getWeight());
        }

        return wt;
    }

    public float getBorder() {
        return (float) this.border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public void setSigma(float sigma) {
        this.twoSigmaSquared = 2.0F * sigma * sigma;
    }

    public void setDistanceNoiseFactor(float factor) {
        this.distanceNoiseFactor = factor;
    }

    public void setAngleNoiseFactor(float factor) {
        this.angleNoiseFactor = factor;
    }

    public void setMaxIterations(int max) {
        maxIterations = max;
    }

    public int findClosest(float x, float y) {
        float minDistance = 10000.0F;
        int index = -1;

        for (int i = 0; i < this.numParticles; ++i) {
            Pose pose = this.particles[i].getPose();
            float distance = (float) Math.sqrt((double) ((pose.getX() - x) * (pose.getX() - x)) + (double) ((pose.getY() - y) * (pose.getY() - y)));
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }

        return index;
    }

    public void dumpObject(DataOutputStream dos) throws IOException {
        dos.writeFloat(this.maxWeight);
        dos.writeInt(this.numParticles());

        for (int i = 0; i < this.numParticles(); ++i) {
            CustomParticle part = this.getParticle(i);
            Pose pose = part.getPose();
            float weight = part.getWeight();
            dos.writeFloat(pose.getX());
            dos.writeFloat(pose.getY());
            dos.writeFloat(pose.getHeading());
            dos.writeFloat(weight);
            dos.flush();
        }

    }

    public int getIterations() {
        return this._iterations;
    }

    public void loadObject(DataInputStream dis) throws IOException {
        this.maxWeight = dis.readFloat();
        this.numParticles = dis.readInt();
        CustomParticle[] newParticles = new CustomParticle[this.numParticles];

        for (int i = 0; i < this.numParticles; ++i) {
            float x = dis.readFloat();
            float y = dis.readFloat();
            float angle = dis.readFloat();
            Pose pose = new Pose(x, y, angle);
            newParticles[i] = new CustomParticle(pose);
            newParticles[i].setWeight(dis.readFloat());
        }

        this.particles = newParticles;
    }

    public void dumpClosest(RangeReadings rr, DataOutputStream dos, float x, float y) throws IOException {
        int closest = this.findClosest(x, y);
        CustomParticle p = this.getParticle(closest);
        dos.writeInt(closest);
        //RangeReadings particleReadings = p.getReadings(rr);
        //particleReadings.dumpObject(dos);
        dos.writeFloat(p.getWeight());
        dos.flush();
    }
}
