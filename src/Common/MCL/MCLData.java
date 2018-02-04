/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package Common.MCL;

import PC.GUI.Displayable;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MCLData implements Transmittable, Displayable {
    private static final String LOG_TAG = MCLData.class.getSimpleName();

    private static final float DISPLAY_TAIL_LENGTH = 30;
    private static final float DISPLAY_TAIL_ANGLE = 10;

    private List<Particle> particles;
    private Pose pose;

    public MCLData() {
    }

    public MCLData(List<Particle> particles, Pose currentPose) {
        this.particles = particles;
        this.pose = currentPose;
    }

    public Pose getPose() {
        return pose;
    }

    @Override
    public synchronized void displayOnGui(@NotNull Graphics g) {
        if (particles != null) {
            g.setColor(Color.BLUE);

            for (Particle particle : particles) {
                displayPoseOnGui(particle.getPose(), g);
                displayParticleWeight(particle, g);
            }
        }

        if (pose != null) {
            g.setColor(Color.RED);
            displayPoseOnGui(pose, g);
        }
    }

    private static void displayPoseOnGui(@NotNull Pose particlePose, @NotNull Graphics g) {
        Point leftEnd = particlePose.pointAt(DISPLAY_TAIL_LENGTH, particlePose.getHeading() + 180 - DISPLAY_TAIL_ANGLE);
        Point rightEnd = particlePose.pointAt(DISPLAY_TAIL_LENGTH, particlePose.getHeading() + 180 + DISPLAY_TAIL_ANGLE);

        int[] xValues = new int[]{
                Math.round(particlePose.getX()),
                Math.round(leftEnd.x),
                Math.round(rightEnd.x)
        };

        int[] yValues = new int[]{
                Math.round(particlePose.getY()),
                Math.round(leftEnd.y),
                Math.round(rightEnd.y)
        };

        g.fillPolygon(xValues, yValues, xValues.length);
    }

    private static void displayParticleWeight(@NotNull Particle particle, @NotNull Graphics g) {
        g.drawString(String.valueOf(particle.getWeight()), Math.round(particle.getPose().getX()), Math.round(particle.getPose().getY()));
    }


    public void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        dos.writeBoolean(pose != null);
        if (pose != null) {
            pose.dumpObject(dos);
        }

        if (particles == null) {
            dos.writeInt(0);
        }
        if (particles != null) {
            dos.writeInt(particles.size());
            for (Particle particle : particles) {
                particle.getPose().dumpObject(dos);
                dos.writeFloat(particle.getWeight());
            }
        }
    }

    public synchronized void loadObject(@NotNull DataInputStream dis) throws IOException {
        if (dis.readBoolean()) {
            this.pose = new Pose();
            this.pose.loadObject(dis);
        }

        int numOfParticles = dis.readInt();

        if (numOfParticles != 0) {
            particles = new ArrayList<>(numOfParticles);

            for (int i = 0; i < numOfParticles; i++) {
                Pose particlePose = new Pose();
                particlePose.loadObject(dis);

                particles.add(new Particle(particlePose, dis.readFloat()));
            }
        }
    }
}