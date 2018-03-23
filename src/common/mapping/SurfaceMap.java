/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.Config;
import pc.displayable.Displayable;
import common.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SurfaceMap implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final Image image;
    private static final PixelReader pixelReader;

    static {
        InputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(Config.IMAGE_PATH);
        } catch (IOException  e) {
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

    public static int getColorAtPoint(float x, float y) {
        try {
            return ColorJavaLejos.getLejosColor(pixelReader.getColor((int) x, (int) (image.getHeight() - y)));
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("x : " + x + ". y : " + y + " " + e);
        }
    }

    @Contract(pure = true)
    public static double getHeight() {
        return image.getHeight();
    }

    @Contract(pure = true)
    public static double getWidth() {
        return image.getWidth();
    }

    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        g.drawImage(image, 0, 0);
    }

    @Override
    public boolean invert() {
        return false;
    }
}