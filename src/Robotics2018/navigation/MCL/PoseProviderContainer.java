package Robotics2018.navigation.MCL;

import Robotics2018.PC.GUI.Displayable;
import Robotics2018.utils.Logger;
import com.sun.istack.internal.NotNull;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PoseProviderContainer implements Transmittable, Displayable {
    private static final String LOG_TAG = PoseProviderContainer.class.getSimpleName();

    protected ParticleSetContainer particleSet;
    protected Pose currentPose;

    public PoseProviderContainer(){
        particleSet = new ParticleSet();
    }

    public PoseProviderContainer(DataInputStream dis) throws IOException{
        this.loadObject(dis);
    }


    public void displayOnGUI(@NotNull Graphics g) {
        if (currentPose == null) {
            Logger.warning(LOG_TAG, "Could not paint robots location because it's null");
            return;
        }

        particleSet.displayOnGUI(g);

        g.setColor(Color.RED);
        new Particle(currentPose).displayOnGUI(g);
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

        particleSet = new ParticleSetContainer(dis);
    }
}
