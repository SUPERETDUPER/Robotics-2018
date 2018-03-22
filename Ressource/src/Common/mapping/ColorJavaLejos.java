/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * Mapping of Javafx colors to lejos colors (int). For example Color.rgb(255,0,0) (javaFX) and Color.RED (lejos)
 */
public class ColorJavaLejos {
    private static final String LOG_TAG = ColorJavaLejos.class.getSimpleName();

    private static final HashMap<Color, Integer> javaToLejosMap = new HashMap<>();

    public static final Color MAP_RED = Color.rgb(237, 28, 36);
    public static final Color MAP_GREEN = Color.rgb(0, 172, 70);
    public static final Color MAP_BLUE = Color.rgb(0, 117, 191);
    public static final Color MAP_YELLOW = Color.rgb(255, 205, 3);

    static {
        javaToLejosMap.put(MAP_RED, lejos.robotics.Color.RED);
        javaToLejosMap.put(MAP_GREEN, lejos.robotics.Color.GREEN);
        javaToLejosMap.put(MAP_BLUE, lejos.robotics.Color.BLUE);
        javaToLejosMap.put(MAP_YELLOW, lejos.robotics.Color.YELLOW);
        javaToLejosMap.put(Color.WHITE, lejos.robotics.Color.WHITE);
        javaToLejosMap.put(Color.BLACK, lejos.robotics.Color.BLACK);
        javaToLejosMap.put(Color.LIGHTGRAY, lejos.robotics.Color.LIGHT_GRAY);
    }


    public static int getLejosColor(Color color) {
        return javaToLejosMap.get(color);
    }
}
