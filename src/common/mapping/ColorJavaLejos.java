/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

/**
 * Mapping of Java swing colors to lejos colors (int).
 * For example new Color(255,0,0) (Swing) might map to Color.RED (lejos)
 * Used by the SurfaceReadings algorithm to convert a pixel on the picture (Swing) to a sensor reading (Lejos)
 */
public class ColorJavaLejos {

    @NotNull
    private static final HashMap<Color, Float> javaToLejosMap = new HashMap<>();

    public static final Color MAP_RED = new Color(237, 28, 36);
    public static final Color MAP_GREEN = new Color(0, 172, 70);
    public static final Color MAP_BLUE = new Color(0, 117, 191);
    public static final Color MAP_YELLOW = new Color(255, 205, 3);

    static {
        javaToLejosMap.put(MAP_RED, 0.85F);
        javaToLejosMap.put(MAP_GREEN, 0.6F);
        javaToLejosMap.put(MAP_BLUE, 0.3F);
        javaToLejosMap.put(MAP_YELLOW, 0.8F);
        javaToLejosMap.put(Color.WHITE, 0.9F);
        javaToLejosMap.put(Color.BLACK, 0.05F);
        javaToLejosMap.put(Color.LIGHT_GRAY, 0.4F);
    }

    static float getLejosColor(Color color) {
        return javaToLejosMap.get(color);
    }
}
