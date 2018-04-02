/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.TestUtils;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Test;

class OffsetTest {

    @Test
    void offset1() {
        Offset offset = new Offset(1, 1);

        Pose pose = new Pose(0, 0, 0);

        TestUtils.assertPointEquals(offset.offset(pose), new Point(1, 1), 0.00001F);
    }

    @Test
    void offset2() {
        Offset offset = new Offset(1, 1);

        Pose pose = new Pose(0, 0, 45);

        TestUtils.assertPointEquals(offset.offset(pose), new Point(0, (float) Math.sqrt(2)), 0.00001F);
    }

    @Test
    void offset3() {
        Offset offset = new Offset(-1, 1);

        TestUtils.assertPointEquals(offset.offset(new Pose()), new Point(-1, 1), 0.0001F);
    }
}