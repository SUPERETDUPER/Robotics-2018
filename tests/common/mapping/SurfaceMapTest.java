/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.Config;
import lejos.robotics.geometry.Rectangle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SurfaceMapTest {
    private final SurfaceMap surfaceMap = new SurfaceMap(Config.PC_IMAGE_PATH);

    @Test
    void getHeight() {
        Assertions.assertEquals(surfaceMap.getImage().getHeight(), 1143);
    }

    @Test
    void getWidth() {
        Assertions.assertEquals(surfaceMap.getImage().getWidth(), 2362);
    }
}