/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import lejos.robotics.geometry.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapDataReader {
    private final float[][] values;

    public MapDataReader(String fileName) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.toString());
        }

        List<String[]> stringValues = new ArrayList<>();

        try {
            while (true) {
                String[] line = reader.readLine().split(",");

                stringValues.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        values = new float[stringValues.size()][stringValues.get(0).length];

        for (int x = 1; x < stringValues.size(); x++) {
            for (int y = 0; y < stringValues.get(0).length; y++) {
                values[x][y] = Float.valueOf(stringValues.get(x)[y]);
            }
        }
    }

    public float getColorAtPoint(Point point) {
        return values[(int) point.x][(int) point.y];
    }

    public boolean contains(Point point){
        return point.x >= 0 && point.x < values.length && point.y >=0 && point.y < values[0].length;
    }
}
