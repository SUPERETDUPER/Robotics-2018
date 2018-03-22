/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void movePose() {
        Pose newPose = Util.movePose(new Pose(10, 10, 90), new Move(30, 0, true));

        Assertions.assertEquals(newPose.getX(), 10);
        Assertions.assertEquals(newPose.getY(), 40);
        Assertions.assertEquals(newPose.getHeading(), 90);

    }

    @Test
    void subtractMove() {
    }
}