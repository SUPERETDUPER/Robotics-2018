/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.logger.Logger;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Reads the colors from the map image
 */
public class SurfaceMap {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    @NotNull
    private final BufferedImage image;

    private final float[][] averageRed;

    private static final int SCAN_RADIUS = 10;

    /**
     * @param filePath file path of the map to read
     */
    public SurfaceMap(String filePath) {
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Unable to read picture");
            throw new RuntimeException(e.toString());
        }

        float[][] pixels = new float[image.getWidth()][image.getHeight()];
        averageRed = new float[image.getWidth()][image.getHeight()];

        for (int x = 1; x < pixels.length; x++) {
            for (int y = 1; y < pixels[0].length; y++) {
                pixels[x][y] = ColorJavaLejos.getLejosColor(new Color(image.getRGB(x, (int) getInvertedY(y))));
            }
        }

        for (int x = 1; x < pixels.length; x++) {
            for (int y = 1; y < pixels[0].length; y++) {
                averageRed[x][y] = calculateAverageRed(pixels, x, y);
            }
        }
    }

    private float calculateAverageRed(float[][] pixels, int centerX, int centerY) {
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

    public float getColorAtPoint(Point point) {
        try {
            return averageRed[(int) point.x][(int) point.y];
        } catch (IndexOutOfBoundsException e) {
            Logger.warning(LOG_TAG, "Can't get color. Out of bounds : x : " + point.x + ". y : " + point.y + " " + e);
            throw new RuntimeException(e);
        }
    }

    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    private boolean contains(float x, float y) {
        return 0 < x && x < averageRed.length && 0 < y && y < averageRed[0].length;
    }

    @Contract(pure = true)
    @NotNull
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Converts a y value from a lejos coordinate to a swing coordinate
     *
     * @param y y value from the lejos coordinates system (bottom = 0)
     * @return y value from swing coordinates system (top = 0)
     */
    @Contract(pure = true)
    private float getInvertedY(float y) {
        return (averageRed[0].length - y);
    }
}