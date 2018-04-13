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
    private final Rectangle boundingRectangle;

    public SurfaceMap(String filePath) {
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Unable to read picture");
            throw new RuntimeException(e.toString());
        }

        boundingRectangle = new Rectangle(0, 0, image.getWidth(), image.getHeight());
    }

    public int getColorAtPoint(int x, int y) {
        try {
            return ColorJavaLejos.getLejosColor(new Color(image.getRGB(x, getInvertedY(y))));
        } catch (IndexOutOfBoundsException e) {
            Logger.warning(LOG_TAG, "Can't get color. Out of bounds : x : " + x + ". y : " + y + " " + e);
            return lejos.robotics.Color.NONE;
        }
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public boolean contains(int x, int y) {
        return x >= 0 && y > 0 && x < image.getWidth() && y <= image.getHeight(); //Weird equals check because y is inverted
    }

    @Contract(pure = true)
    private int getInvertedY(int y) {
        return (image.getHeight() - y);
    }

    @Contract(pure = true)
    @NotNull
    public BufferedImage getImage() {
        return image;
    }
}