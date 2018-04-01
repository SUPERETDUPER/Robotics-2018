/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.logger.Logger;
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
            Logger.error(LOG_TAG, "x : " + x + ". y : " + y + " " + e);
            throw new IndexOutOfBoundsException("x : " + x + ". y : " + y + " " + e);
        }
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