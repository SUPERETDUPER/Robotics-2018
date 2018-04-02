/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.Config;
import lejos.robotics.geometry.Rectangle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SurfaceMapTest {
    private final Rectangle boundingRectangle = new SurfaceMap(Config.PC_IMAGE_PATH).getBoundingRectangle();

    @Test
    void getHeight() {
        Assertions.assertEquals(boundingRectangle.getHeight(), 1143);
    }

    @Test
    void getWidth() {
        Assertions.assertEquals(boundingRectangle.getWidth(), 2362);
    }
}