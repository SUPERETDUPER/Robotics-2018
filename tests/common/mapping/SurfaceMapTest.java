/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SurfaceMapTest {

    @Test
    void getHeight() {
        Assertions.assertEquals(new SurfaceMap(Config.PC_IMAGE_PATH).getHeight(), 1143);
    }

    @Test
    void getWidth() {
        Assertions.assertEquals(new SurfaceMap(Config.PC_IMAGE_PATH).getWidth(), 2362);
    }
}