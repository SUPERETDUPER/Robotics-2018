/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapDataReader {
    private final float[][] values;

    public MapDataReader(String fileName) {
        BufferedReader reader = getReader(fileName);
        List<String[]> stringValues = readFromReader(reader);
        values = convertToValues(stringValues);
    }

    public float getColorAtPoint(Point point) {
        return values[(int) point.y][(int) point.x];
    }

    public boolean contains(Point point) {
        return 0 <= point.x && point.x < values[0].length && 0 <= point.y && point.y < values.length;
    }

    @NotNull
    private static BufferedReader getReader(String fileName) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.toString());
        }
        return reader;
    }

    @NotNull
    private static List<String[]> readFromReader(BufferedReader reader) {
        List<String[]> stringValues = new ArrayList<>();

        try {
            String line = reader.readLine();

            while (line != null) {
                String[] splitLine = line.split(",");
                stringValues.add(splitLine);

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringValues;
    }

    private static float[][] convertToValues(@NotNull List<String[]> stringValues) {
        float[][] values = new float[stringValues.size()][stringValues.get(0).length];

        for (int y = 0; y < stringValues.size(); y++) {
            for (int x = 0; x < stringValues.get(0).length; x++) {
                values[y][x] = Float.valueOf(stringValues.get(y)[x]);
            }
        }

        return values;
    }
}
