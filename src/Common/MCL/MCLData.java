package Common.MCL;

import Common.utils.Logger;
import PC.GUI.Displayable;
import com.sun.istack.internal.NotNull;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MCLData implements Transmittable, Displayable {
    public static final int NUM_PARTICLES = 300;
    private static final float DISPLAY_TAIL_LENGTH = 30;
    private static final float DISPLAY_TAIL_ANGLE = 20;

    private static final String LOG_TAG = MCLData.class.getSimpleName();

    private ArrayList<Particle> particles;
    private Pose currentPose;

    public MCLData() {
    }

    public MCLData(ArrayList<Particle> particles, Pose currentPose) {
        this.particles = particles;
        this.currentPose = currentPose;
    }

    public Pose getCurrentPose() {
        return currentPose;
    }

    @Override
    public synchronized void displayOnGui(Graphics g) {
        if (particles != null) {
            g.setColor(Color.BLUE);

            for (Particle particle : particles) {
                displayParticleOnGui(particle, g);
            }
        } else {
            Logger.warning(LOG_TAG, "Could not display particles because is null");
        }

        if (currentPose != null) {
            g.setColor(Color.RED);
            displayParticleOnGui(new Particle(currentPose, -1), g);
        } else {
            Logger.warning(LOG_TAG, "Could not paint robots location because it's null");
        }
    }

    private static void displayParticleOnGui(Particle particle, Graphics g) {
        Pose particlePose = particle.getPose();

        Point leftEnd = particlePose.pointAt(DISPLAY_TAIL_LENGTH, particlePose.getHeading() + 180 - DISPLAY_TAIL_ANGLE / 2);
        Point rightEnd = particlePose.pointAt(DISPLAY_TAIL_LENGTH, particlePose.getHeading() + 180 + DISPLAY_TAIL_ANGLE / 2);

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
        if (particle.getWeight() != -1) {
            g.drawString(String.valueOf(particle.getWeight()), Math.round(particlePose.getX()), Math.round(particlePose.getY()));
        }
    }


    public void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        dos.writeBoolean(currentPose != null);
        if (currentPose != null) {
            currentPose.dumpObject(dos);
        }

        dos.writeBoolean(particles != null);
        if (particles != null) {
            for (Particle particle : particles) {
                particle.getPose().dumpObject(dos);
                dos.writeFloat(particle.getWeight());
            }
        }
    }

    public synchronized void loadObject(@NotNull DataInputStream dis) throws IOException {
        if (dis.readBoolean()) {
            this.currentPose = new Pose();
            this.currentPose.loadObject(dis);
        }

        if (dis.readBoolean()) {
            particles = new ArrayList<>(NUM_PARTICLES);

            for (int i = 0; i < NUM_PARTICLES; i++) {
                Pose particlePose = new Pose();
                particlePose.loadObject(dis);

                particles.add(new Particle(particlePose, dis.readFloat()));
            }
        }
    }
}