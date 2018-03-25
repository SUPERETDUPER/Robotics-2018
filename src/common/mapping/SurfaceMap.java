/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.Config;
import common.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.jetbrains.annotations.Contract;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SurfaceMap {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final Image image;
    private static final PixelReader pixelReader;

    static {
        InputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(Config.IMAGE_PATH);
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Unable to read picture");
            throw new RuntimeException(e.toString());
        }

        image = new Image(fileInputStream);
        pixelReader = image.getPixelReader();

        try {
            fileInputStream.close();
        } catch (IOException e) {
            Logger.warning(LOG_TAG, "Could not close file input stream");
        }
    }

    public static int getColorAtPoint(int x, int y) {
        try {
            return ColorJavaLejos.getLejosColor(pixelReader.getColor(x, (int) (image.getHeight() - y)));
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("x : " + x + ". y : " + y + " " + e);
        }
    }

    public static boolean contains(int x, int y) {
        return x >= 0 && y > 0 && x < image.getWidth() && y <= image.getHeight(); //Weird equals check because y is inverted
    }

    @Contract(pure = true)
    public static double getHeight() {
        return image.getHeight();
    }

    @Contract(pure = true)
    public static double getWidth() {
        return image.getWidth();
    }

    public static Image getImage() {
        return image;
    }
}