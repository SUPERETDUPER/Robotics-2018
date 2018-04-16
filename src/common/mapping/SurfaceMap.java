/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.logger.Logger;
import lejos.robotics.geometry.Rectangle;
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
    }

    public int getColorAtPoint(int x, int y) {
        try {
            return ColorJavaLejos.getLejosColor(new Color(image.getRGB(x, getInvertedY(y))));
        } catch (IndexOutOfBoundsException e) {
            Logger.warning(LOG_TAG, "Can't get color. Out of bounds : x : " + x + ". y : " + y + " " + e);
            return lejos.robotics.Color.NONE;
        }
    }

    /**
     * @return A rectangle starting at (0,0) that bounds the image
     */
    public Rectangle getBoundingRectangle() {
        return new Rectangle(0, 0, image.getWidth(), image.getHeight());
    }

    public boolean isPointIn(int x, int y) {
        return 0 < x && x < image.getWidth() && 0 < y && y < image.getHeight(); //Weird equals check for y values because y is inverted
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
    private int getInvertedY(int y) {
        return (image.getHeight() - y);
    }
}