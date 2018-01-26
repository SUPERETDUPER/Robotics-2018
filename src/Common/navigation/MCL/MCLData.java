package Common.navigation.MCL;

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
    static final int NUM_PARTICLES = 300;
    private static final String LOG_TAG = MCLData.class.getSimpleName();
    private static final float DISPLAY_TAIL_LENGTH = 30;
    private static final float DISPLAY_TAIL_ANGLE = 20;
    ArrayList<Particle> particles;
    Pose currentPose;

    private static void displayParticleOnGui(Pose particlePose, Graphics g) {
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
    }

    @Override
    public void displayOnGUI(Graphics g) {
        if (particles != null) {
            g.setColor(Color.BLUE);

            for (Particle particle : particles) {
                displayParticleOnGui(particle.getPose(), g);
            }
        } else {
            Logger.warning(LOG_TAG, "Could not display particles because is null");
        }

        if (currentPose != null) {
            g.setColor(Color.RED);
            displayParticleOnGui(currentPose, g);
        } else {
            Logger.warning(LOG_TAG, "Could not paint robots location because it's null");
        }
    }

    public void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        dos.writeBoolean(currentPose != null);
        if (currentPose != null) {
            dos.writeFloat(currentPose.getX());
            dos.writeFloat(currentPose.getY());
            dos.writeFloat(currentPose.getHeading());
        }

        dos.writeBoolean(particles != null);
        if (particles != null) {
            for (Particle particle : particles) {
                particle.getPose().dumpObject(dos);
                dos.writeFloat(particle.getWeight());
            }
        }
    }

    public void loadObject(@NotNull DataInputStream dis) throws IOException {
        if (dis.readBoolean()) {
            this.currentPose = new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat());
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
