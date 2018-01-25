package Robotics2018.navigation.MCL;

import Robotics2018.PC.GUI.Displayable;
import Robotics2018.utils.Logger;
import com.sun.istack.internal.NotNull;
import lejos.robotics.Transmittable;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ParticleSetContainer implements Displayable, Transmittable, Iterable<Particle>{
    private static final String LOG_TAG = ParticleSetContainer.class.getSimpleName();

    protected ArrayList<Particle> particles;
    protected static final int numParticles = 200;

    ParticleSetContainer(){

    }

    ParticleSetContainer(DataInputStream dis) throws IOException{
        this.loadObject(dis);
    }

    @Override
    @NotNull
    public Iterator<Particle> iterator() {
        return particles.iterator();
    }

    public void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        for (Particle particle : particles) {
            particle.dumpObject(dos);
        }
    }

    public void loadObject(@NotNull DataInputStream dis) throws IOException {
        ArrayList<Particle> newParticles = new ArrayList<>(numParticles);

        for (int i = 0; i < numParticles; ++i) {
            newParticles.add(new Particle(dis));
        }

        this.particles = newParticles;
    }

    public void displayOnGUI(@NotNull Graphics g) {
        g.setColor(Color.BLUE);

        for (Particle particle : particles) {

            if (particle == null) {
                Logger.warning(LOG_TAG, "Could not display particle because is null");
                continue;
            }

            particle.displayOnGUI(g);
        }
    }
}
