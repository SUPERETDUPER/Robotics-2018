/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SurfaceMapReading {
    private final float[][] values;

    public SurfaceMapReading(String fileName) {
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

    public float getValue(Point point) {
        return values[point.x][point.y];
    }

    public boolean contains(Point point){
        return point.x >= 0 && point.x < values.length && point.y >=0 && point.y < values[0].length;
    }
}
