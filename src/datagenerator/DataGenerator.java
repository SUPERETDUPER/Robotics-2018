/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package datagenerator;

import common.Config;
import common.mapping.ColorJavaLejos;
import common.mapping.SurfaceMap;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Reads the colors from the map image
 */
public class DataGenerator {
    private static final String LOG_TAG = DataGenerator.class.getSimpleName();

    private static final int SCAN_RADIUS = 10;

    private static int width;
    private static int height;

    public static void main(String[] args) {
        SurfaceMap surfaceMap = new SurfaceMap();

        width = surfaceMap.getImage().getWidth();
        height = surfaceMap.getImage().getHeight();

        writeData(
                createString(
                        getAveragePixels(
                                getPixels(surfaceMap)
                        )
                )
        );
    }

    private static float[][] getPixels(SurfaceMap surfaceMap) {
        //Save each pixel to a 2D array
        float[][] pixels = new float[height][width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[y][x] = ColorJavaLejos.getLejosColor(new Color(surfaceMap.getImage().getRGB(x, (int) getInvertedY(y)))); //Convert RGB to red value
            }
        }

        return pixels;
    }

    private static float[][] getAveragePixels(float[][] pixels) {
        //Calculate average value
        float[][] averageRed = new float[height][width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                averageRed[y][x] = calculateAverageForPixel(pixels, x, y);
            }
        }
        return averageRed;
    }

    private static float calculateAverageForPixel(float[][] pixels, int centerX, int centerY) {
        float sum = 0;
        int counter = 0;

        //Loop through every pixel in the circle
        for (int x = centerX - SCAN_RADIUS; x <= centerX + SCAN_RADIUS; x++) {
            for (int y = centerY - SCAN_RADIUS; y <= centerY + SCAN_RADIUS; y++) {
                if (new Point(centerX, centerY).distance(x, y) < SCAN_RADIUS && contains(x, y)) { //If (x,y) within circle
                    sum += pixels[y][x];
                    counter++;
                }
            }
        }

        return Math.round(100 * sum / counter) / 100.0F;
    }

    @NotNull
    private static String createString(float[][] averageRed) {
        //Create a string with the data
        StringBuilder writeData = new StringBuilder();

        for (float[] row : averageRed) {
            for (float pixel : row) {
                writeData.append(pixel).append(",");
            }
            writeData.append("\n");
        }
        return writeData.toString();
    }

    private static void writeData(String data) {
        //Write the data to a file
        FileOutputStream outputStream;

        try {
            File file = new File(Config.DATA_PC_PATH);

            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to write to file " + e);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Contract(pure = true)
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