/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package datagenerator;

import common.logger.Logger;
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

    public static void main(String[] args) {
        SurfaceMap surfaceMap = new SurfaceMap();

        float[][] pixels = new float[surfaceMap.getImage().getWidth()][surfaceMap.getImage().getHeight()];
        averageRed = new float[surfaceMap.getImage().getWidth()][surfaceMap.getImage().getHeight()];

        for (int x = 1; x < pixels.length; x++) {
            for (int y = 1; y < pixels[0].length; y++) {
                pixels[x][y] = ColorJavaLejos.getLejosColor(new Color(surfaceMap.getImage().getRGB(x, (int) getInvertedY(y))));
            }
        }

        for (int x = 1; x < pixels.length; x++) {
            for (int y = 1; y < pixels[0].length; y++) {
                averageRed[x][y] = calculateAverageRed(pixels, x, y);
            }
        }

        StringBuilder writeData = new StringBuilder();

        for (float[] row : averageRed) {
            for (float pixel : row) {
                writeData.append(pixel).append(",");
            }
            writeData.append("\n");
        }

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(new File("/data.txt"));
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
                if (new Point(centerX, centerY).distance(x, y) < SCAN_RADIUS && contains(new Point(x, y))) { //If (x,y) within circle
                    counter++;

                    sum += pixels[x][y];
                }
            }
        }

        return sum / counter;
    }

    private static boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    private static boolean contains(float x, float y) {
        return 0 < x && x < averageRed.length && 0 < y && y < averageRed[0].length;
    }

    /**
     * Converts a y value from a lejos coordinate to a swing coordinate
     *
     * @param y y value from the lejos coordinates system (bottom = 0)
     * @return y value from swing coordinates system (top = 0)
     */
    @Contract(pure = true)
    private static float getInvertedY(float y) {
        return (averageRed[0].length - y);
    }
}