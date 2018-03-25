/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.EventTypes;
import ev3.DataSender;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

class ConnectionTest {

    @Test
    void listen() {
        final Pose poseToSend = new Pose(10, 10, 10);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DataSender.init(out);
        DataSender.sendCurrentPose(poseToSend);

        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());

        DataReader.init(input, new DataChangeListener() {
            @Override
            public void dataChanged(EventTypes event, DataInputStream dis) throws IOException {
                Assertions.assertEquals(event, EventTypes.CURRENT_POSE);

                Pose pose = new Pose();
                pose.loadObject(dis);

                poseEquals(poseToSend, pose);
            }

            @Override
            public void connectionLost() {

            }
        });

        DataReader.read();
    }

    @Test
    void poseEquals(Pose pose1, Pose pose2) {
        Assertions.assertEquals(pose1.getX(), pose2.getX());
        Assertions.assertEquals(pose1.getY(), pose2.getY());
        Assertions.assertEquals(pose1.getHeading(), pose2.getHeading());
    }
}