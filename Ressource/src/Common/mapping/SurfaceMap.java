/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import Common.Config;
import Common.GUI.Displayable;
import Common.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;

public class SurfaceMap implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static Image image;
    private static PixelReader pixelReader;

    static {
        try {
            FileInputStream fileInputStream = new FileInputStream(Config.IMAGE_PATH);
            image = new Image(fileInputStream);
            pixelReader = image.getPixelReader();
            fileInputStream.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Unable to read picture");
        }
    }

    public static int getColorAtPoint(float x, float y) {
        return ColorJavaLejos.getLejosColor(pixelReader.getColor((int) x, (int) (image.getHeight() - y)));
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