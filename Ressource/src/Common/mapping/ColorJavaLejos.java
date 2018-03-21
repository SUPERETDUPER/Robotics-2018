/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import Common.Logger;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * Mapping of Javafx colors to lejos colors (int). For example Color.rgb(255,0,0) (javaFX) and Color.RED (lejos)
 */
public class ColorJavaLejos {
    private static final String LOG_TAG = ColorJavaLejos.class.getSimpleName();

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

    public static Color getJavaColor(int color) {
        for (Color javaColor : javaToLejosMap.keySet()) {
            if (javaToLejosMap.get(javaColor).equals(color)) {
                return javaColor;
            }
        }

        Logger.error(LOG_TAG, "Java color does not exist");
        return null;
    }
}
