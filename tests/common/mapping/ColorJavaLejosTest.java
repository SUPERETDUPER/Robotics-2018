/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ColorJavaLejosTest {

    @Test
    void getLejosColor() {
        Assertions.assertEquals(ColorJavaLejos.getLejosColor(Color.BLACK), lejos.robotics.Color.BLACK);
        Assertions.assertEquals(ColorJavaLejos.getLejosColor(Color.WHITE), lejos.robotics.Color.WHITE);
    }
}