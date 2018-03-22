/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    void movePoseTravel() {
        Pose newPose = Util.movePose(new Pose(10, 10, 90), new Move(30, 0, true));

        Assertions.assertEquals(newPose.getX(), 10);
        Assertions.assertEquals(newPose.getY(), 40);
        Assertions.assertEquals(newPose.getHeading(), 90);
    }

    @Test
    void movePoseRotate() {
        Pose newPose = Util.movePose(new Pose(10, 10, 90), new Move(0, 180, true));

        Assertions.assertEquals(newPose.getX(), 10);
        Assertions.assertEquals(newPose.getY(), 10);
        Assertions.assertEquals(newPose.getHeading(), 270);
    }

    @Test
    void subtractMoveTravel() {
        Move move1 = new Move(100, 0, true);
        Move move2 = new Move(50, 0, true);

        Move result = Util.subtractMove(move1, move2);

        Assertions.assertEquals(result.getDistanceTraveled(), 50);
        Assertions.assertEquals(result.getAngleTurned(), 0);
        Assertions.assertEquals(result.getMoveType(), Move.MoveType.TRAVEL);
    }

    @Test
    void subtractMoveRotate() {
        Move move1 = new Move(0, 180, true);
        Move move2 = new Move(0, 90, true);

        Move result = Util.subtractMove(move1, move2);

        Assertions.assertEquals(result.getDistanceTraveled(), 0);
        Assertions.assertEquals(result.getAngleTurned(), 90);
        Assertions.assertEquals(result.getMoveType(), Move.MoveType.ROTATE);
    }
}