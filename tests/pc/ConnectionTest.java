/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.EventType;
import common.TestUtils;
import ev3.communication.DataSender;
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
            public void dataChanged(EventType event, DataInputStream dis) throws IOException {
                Assertions.assertEquals(event, EventType.CURRENT_POSE);

                Pose loadedPose = new Pose();
                loadedPose.loadObject(dis);

                TestUtils.assertPoseEqual(poseToSend, loadedPose);
            }

            @Override
            public void connectionLost() {

            }
        });

        DataReader.read();
    }
}