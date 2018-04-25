/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package datagenerator;

import common.mapping.ColorJavaLejos;
import common.mapping.SurfaceMap;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.Contract;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Reads the colors from the map image
 */
public class DataGenerator {
    private static final String LOG_TAG = DataGenerator.class.getSimpleName();

    private static float[][] averageRed;

    private static final int SCAN_RADIUS = 10;

    private static int width;
    private static int height;

    public static void main(String[] args) {
        SurfaceMap surfaceMap = new SurfaceMap();

        width = surfaceMap.getImage().getWidth();
        height = surfaceMap.getImage().getHeight();

        //Save each pixel to a 2D array
        float[][] pixels = new float[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = ColorJavaLejos.getLejosColor(new Color(surfaceMap.getImage().getRGB(x, (int) getInvertedY(y)))); //Convert RGB to red value
            }
        }

        //Calculate average value
        averageRed = new float[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                averageRed[x][y] = calculateAverageRed(pixels, x, y);
            }
        }

        //Create a string with the data
        StringBuilder writeData = new StringBuilder();

        for (float[] row : averageRed) {
            for (float pixel : row) {
                writeData.append(pixel).append(",");
            }
            writeData.append("\n");
        }

        //Write the data to a file
        FileOutputStream outputStream;

        try {
            File file = new File("/data.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            outputStream = new FileOutputStream(file);
            outputStream.write(writeData.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to write to file");
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float calculateAverageRed(float[][] pixels, int centerX, int centerY) {
        float sum = 0;
        int counter = 0;

        //Loop through every pixel in the circle
        for (int x = centerX - SCAN_RADIUS; x <= centerX + SCAN_RADIUS; x++) {
            for (int y = centerY - SCAN_RADIUS; y <= centerY + SCAN_RADIUS; y++) {
                if (new Point(centerX, centerY).distance(x, y) < SCAN_RADIUS && contains(x, y)) { //If (x,y) within circle
                    sum += pixels[x][y];
                    counter++;
                }
            }
        }

        return sum / counter;
    }

    private static boolean contains(float x, float y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    /**
     * Converts a y value from a lejos coordinate to a swing coordinate
     *
     * @param y y value from the lejos coordinates system (bottom = 0)
     * @return y value from swing coordinates system (top = 0)
     */
    @Contract(pure = true)
    private static float getInvertedY(float y) {
        return (height - 1 - y);
    }
}