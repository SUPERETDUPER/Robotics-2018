/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import javafx.scene.paint.Color;

import java.util.HashMap;

public class LejosToJavaColor {

    private static final HashMap<Color, Integer> javaToLejosMap = new HashMap<>();

    static {
        javaToLejosMap.put(Color.rgb(237, 28, 36), lejos.robotics.Color.RED);
        javaToLejosMap.put(Color.rgb(0, 172, 70), lejos.robotics.Color.GREEN);
        javaToLejosMap.put(Color.rgb(0, 117, 191), lejos.robotics.Color.BLUE);
        javaToLejosMap.put(Color.rgb(255, 205, 3), lejos.robotics.Color.YELLOW);
        javaToLejosMap.put(Color.WHITE, lejos.robotics.Color.WHITE);
        javaToLejosMap.put(Color.BLACK, lejos.robotics.Color.BLACK);
    }


    public static int getLejosColor(Color color) {
        return javaToLejosMap.get(color);
    }

    public static Color getJavaColor(lejos.robotics.Color color) {
        //TODO
        return null;
    }
}
