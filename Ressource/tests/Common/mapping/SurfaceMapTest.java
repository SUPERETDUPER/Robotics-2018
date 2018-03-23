/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SurfaceMapTest {

    @Test
    void getHeight() {
        Assertions.assertEquals(SurfaceMap.getHeight(), 1143);
    }

    @Test
    void getWidth() {
        Assertions.assertEquals(SurfaceMap.getWidth(), 2362);
    }
}