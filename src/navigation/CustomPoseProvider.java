//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package navigation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Rectangle2D;
import lejos.robotics.geometry.RectangleInt32;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;

public class CustomPoseProvider implements PoseProvider, MoveListener, Transmittable {
    private CustomParticleSet particles;
    private int numParticles;
    private float _x;
    private float _y;
    private float _heading;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private double varX;
    private double varY;
    private double varH;
    private boolean updated;
    private CustomPoseProvider.Updater updater = new CustomPoseProvider.Updater();
    private int border;
    private boolean debug = false;
    private boolean busy = false;
    private float BIG_FLOAT = 1000000.0F;
    private RangeReadings readings;
    private boolean lost = false;
    private boolean incomplete = true;

    public CustomPoseProvider(MoveProvider mp, int numParticles, int border) {
        this.numParticles = numParticles;
        this.border = border;
        if (numParticles > 0) {
            this.particles = new CustomParticleSet(numParticles, border);
        }

        if (mp != null) {
            mp.addMoveListener(this);
        }

        this.updated = false;
    }

    public CustomPoseProvider(int numParticles, int border) {
        this.numParticles = numParticles;
        this.border = border;
        this.updated = false;
    }

    public void setInitialPose(Pose aPose, float radiusNoise, float headingNoise) {
        this._x = aPose.getX();
        this._y = aPose.getY();
        this._heading = aPose.getHeading();
        this.particles = new CustomParticleSet(this.numParticles, aPose, radiusNoise, headingNoise);
    }

    public void setInitialPose(RangeReadings readings, float sigma) {
        if (this.debug) {
            System.out.println("CustomPP set Initial pose called ");
        }

        float minWeight = 0.3F;
        this.particles = new CustomParticleSet(this.numParticles, this.border, 2.0F * sigma * sigma, minWeight);
        this.updated = true;
    }

    public void setDebug(boolean on) {
        this.debug = on;
    }

    public void setPose(Pose aPose) {
        this.setInitialPose(aPose, 1.0F, 1.0F);
        this.updated = true;
    }

    public CustomParticleSet getParticles() {
        CustomParticleSet var1 = this.particles;
        synchronized(this.particles) {
            return this.particles;
        }
    }

    public RangeReadings getReadings() {
        return this.readings;
    }

    public void setParticles(CustomParticleSet particles) {
        this.particles = particles;
        this.numParticles = particles.numParticles();
    }

    public void generateParticles() {
        this.particles = new CustomParticleSet(this.numParticles, this.border);
    }

    public void moveStarted(Move event, MoveProvider mp) {
        this.updated = false;
    }

    public void moveStopped(Move event, MoveProvider mp) {
        if (this.debug) {
            System.out.println("Custom move stopped");
        }

        this.particles.applyMove(event);
    }

    public boolean update() {
        if (this.debug) {
            System.out.println("CustomPP update called ");
        }

        this.updated = false;

        // TODO : GET readings
        // this.readings = this.scanner.getRangeValues();
        if (this.debug) {
            this.readings.printReadings();
        }

        this.incomplete = this.readings.incomplete();
        return !this.incomplete && this.update(this.readings);

    }

    public boolean update(RangeReadings readings) {
        if (this.debug) {
            System.out.println("CustomPP update readings called ");
        }

        this.updated = false;
        this.incomplete = readings.incomplete();
        if (this.incomplete) {
            return false;
        } else {
            if (this.debug) {
                System.out.println("update readings incomplete " + this.incomplete);
            }

            boolean goodPose = false;
            goodPose = this.particles.calculateWeights();
            if (this.debug) {
                System.out.println(" max Weight " + this.particles.getMaxWeight() + " Good pose " + goodPose);
            }

            if (!goodPose) {
                if (this.debug) {
                    System.out.println("Sensor data improbable from this pose ");
                }

                return false;
            } else {
                goodPose = this.particles.resample();
                this.updated = goodPose;
                if (this.debug) {
                    if (goodPose) {
                        System.out.println("Resample done");
                    } else {
                        System.out.println("Resample failed");
                    }
                }

                return goodPose;
            }
        }
    }

    public boolean isUpdated() {
        return this.updated;
    }

    public boolean isLost() {
        return this.lost;
    }

    public boolean incompleteRanges() {
        return this.incomplete;
    }

    public float getXRange() {
        return this.getMaxX() - this.getMinX();
    }

    public float getYRange() {
        return this.getMaxY() - this.getMinY();
    }

    public Pose getPose() {
        if (this.debug) {
            System.out.println("Custom call update; updated? " + this.updated + " busy " + this.busy);
        }

        if (!this.updated) {
            for(; this.busy; Thread.yield()) {
                if (this.debug) {
                    System.out.println("Custom Busy");
                }
            }

            if (this.debug) {
                System.out.println("Custom call update; updated? " + this.updated);
            }

            if (!this.updated) {
                this.update();
            }
        }

        this.estimatePose();
        return new Pose(this._x, this._y, this._heading);
    }

    public Pose getEstimatedPose() {
        return new Pose(this._x, this._y, this._heading);
    }

    public void estimatePose() {
        float totalWeights = 0.0F;
        float estimatedX = 0.0F;
        float estimatedY = 0.0F;
        float estimatedAngle = 0.0F;
        this.varX = 0.0D;
        this.varY = 0.0D;
        this.varH = 0.0D;
        this.minX = this.BIG_FLOAT;
        this.minY = this.BIG_FLOAT;
        this.maxX = -this.BIG_FLOAT;
        this.maxY = -this.BIG_FLOAT;

        for(int i = 0; i < this.numParticles; ++i) {
            Pose p = this.particles.getParticle(i).getPose();
            float x = p.getX();
            float y = p.getY();
            float weight = 1.0F;
            estimatedX += x * weight;
            this.varX += (double)(x * x * weight);
            estimatedY += y * weight;
            this.varY += (double)(y * y * weight);
            float head = p.getHeading();
            estimatedAngle += head * weight;
            this.varH += (double)(head * head * weight);
            totalWeights += weight;
            if (x < this.minX) {
                this.minX = x;
            }

            if (x > this.maxX) {
                this.maxX = x;
            }

            if (y < this.minY) {
                this.minY = y;
            }

            if (y > this.maxY) {
                this.maxY = y;
            }
        }

        estimatedX /= totalWeights;
        this.varX /= (double)totalWeights;
        this.varX -= (double)(estimatedX * estimatedX);
        estimatedY /= totalWeights;
        this.varY /= (double)totalWeights;
        this.varY -= (double)(estimatedY * estimatedY);
        estimatedAngle /= totalWeights;
        this.varH /= (double)totalWeights;

        for(this.varH -= (double)(estimatedAngle * estimatedAngle); estimatedAngle > 180.0F; estimatedAngle -= 360.0F) {
            ;
        }

        while(estimatedAngle < -180.0F) {
            estimatedAngle += 360.0F;
        }

        this._x = estimatedX;
        this._y = estimatedY;
        this._heading = estimatedAngle;
    }

    public RangeReadings getRangeReadings() {
        return this.readings;
    }

    public Rectangle2D getErrorRect() {
        return new RectangleInt32((int)this.minX, (int)this.minY, (int)(this.maxX - this.minX), (int)(this.maxY - this.minY));
    }

    public float getMaxX() {
        return this.maxX;
    }

    public float getMinX() {
        return this.minX;
    }

    public float getMaxY() {
        return this.maxY;
    }

    public float getMinY() {
        return this.minY;
    }

    public float getSigmaX() {
        return (float)Math.sqrt(this.varX);
    }

    public float getSigmaY() {
        return (float)Math.sqrt(this.varY);
    }

    public float getSigmaHeading() {
        return (float)Math.sqrt(this.varH);
    }

    public void dumpObject(DataOutputStream dos) throws IOException {
        dos.writeFloat(this._x);
        dos.writeFloat(this._y);
        dos.writeFloat(this._heading);
        dos.writeFloat(this.minX);
        dos.writeFloat(this.maxX);
        dos.writeFloat(this.minY);
        dos.writeFloat(this.maxY);
        dos.writeFloat((float)this.varX);
        dos.writeFloat((float)this.varY);
        dos.writeFloat((float)this.varH);
        dos.flush();
    }

    public void loadObject(DataInputStream dis) throws IOException {
        this._x = dis.readFloat();
        this._y = dis.readFloat();
        this._heading = dis.readFloat();
        this.minX = dis.readFloat();
        this.maxX = dis.readFloat();
        this.minY = dis.readFloat();
        this.maxY = dis.readFloat();
        this.varX = (double)dis.readFloat();
        this.varY = (double)dis.readFloat();
        this.varH = (double)dis.readFloat();
        if (this.debug) {
            System.out.println("Estimate = " + this.minX + " , " + this.maxX + " , " + this.minY + " , " + this.maxY);
        }

    }

    public boolean isBusy() {
        return this.busy;
    }

    class Updater extends Thread {
        boolean keepGoing = true;
        ArrayList<Move> events = new ArrayList();

        Updater() {
        }

        public void moveStopped(Move theEvent) {
            this.events.add(theEvent);
        }

        public void run() {
            while(this.keepGoing) {
                for(; !this.events.isEmpty(); this.events.remove(0)) {
                    Move event = (Move)this.events.get(0);
                    if (event == null) {
                        System.out.println("CustomPoseProvider: null event");
                    } else {
                        if (CustomPoseProvider.this.debug) {
                            System.out.println("Updater move stop " + event.getMoveType());
                        }

                        CustomPoseProvider.this.busy = true;
                        synchronized(CustomPoseProvider.this.particles) {
                            CustomPoseProvider.this.particles.applyMove(event);
                        }

                        if (CustomPoseProvider.this.debug) {
                            System.out.println("applied move ");
                        }
                    }
                }

                CustomPoseProvider.this.busy = false;
                Thread.yield();
            }

        }
    }
}