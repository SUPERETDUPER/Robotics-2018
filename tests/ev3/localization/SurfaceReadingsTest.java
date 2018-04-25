/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.Config;
import common.ConnectionUtil;
import common.TransmittableType;
import common.mapping.MapDataReader;
import common.mapping.SurfaceMap;
import common.particles.MCLData;
import common.particles.Particle;
import ev3.communication.PCDataSender;
import ev3.navigation.Offset;
import lejos.robotics.Color;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Test;
import pc.PCMain;

import java.util.ArrayList;

class SurfaceReadingsTest {

    /**
     * Requires PC to run to test
     */
    @Test
    void readingsAcrossTheMap() {
        new Thread() {
            @Override
            public void run() {
                PCMain.main(null);
            }
        }.start();

        ArrayList<Particle> particles = new ArrayList<>();

        SurfaceMap surfaceMap = new SurfaceMap();
        MapDataReader mapDataReader = new MapDataReader(Config.DATA_PC_PATH);
        SurfaceReadings readings = new SurfaceReadings(mapDataReader, Color.GREEN, new Offset(0, 0));

        for (int x = 0; x < surfaceMap.getImage().getWidth(); x += 10) {
            for (int y = 0; y < surfaceMap.getImage().getHeight(); y += 10) {
                Pose pose = new Pose(x, y, 0);
                particles.add(new Particle(x, y, 0, readings.calculateWeight(pose)));
            }
        }

        new PCDataSender(ConnectionUtil.createOutputStream(ConnectionUtil.createServerSocket(Config.PORT_TO_CONNECT_ON_EV3))).sendTransmittable(
                TransmittableType.MCL_DATA,
                new MCLData(particles.toArray(new Particle[0]), new Pose())
        );
    }
}