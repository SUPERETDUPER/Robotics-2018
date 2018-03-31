/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.TransmittableType;
import common.mapping.SurfaceMap;
import common.particles.MCLData;
import common.particles.Particle;
import ev3.communication.ComManager;
import ev3.communication.PCDataSender;
import lejos.robotics.Color;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class SurfaceReadingsTest {

    /**
     * Requires PC to run to test
     */
    @Test
    void readingsAcrossTheMap() {
        ArrayList<Particle> particles = new ArrayList<>();

        SurfaceReadings readings = new SurfaceReadings(Color.WHITE);

        for (int x = 0; x < SurfaceMap.getWidth(); x += 10) {
            for (int y = 0; y < SurfaceMap.getHeight(); y += 10) {
                Pose pose = new Pose(x, y, 0);
                particles.add(new Particle(x, y, 0, readings.calculateWeight(pose)));
            }
        }

        new PCDataSender(ComManager.getConnection(), null).sendTransmittable(
                TransmittableType.MCL_DATA,
                new MCLData(particles.toArray(new Particle[0]), null)
        );
    }
}