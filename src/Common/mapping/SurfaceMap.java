/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import Common.Config;
import Common.Logger;
import PC.GUI.GUILayers.Displayable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;

public class SurfaceMap implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static Image image;

    static {
        try {
            image = new Image(new FileInputStream(Config.IMAGE_PATH));
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Unable to read picture");
        }
    }

    public static boolean contains(@NotNull Point point) {
        return point.x >= 0 && point.x < image.getWidth() && point.y >= 0 && point.y < image.getHeight();
    }

    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        g.drawImage(image, 0, 0);
    }

    public static int getColorAtPoint(Point point) {
        //TODO Fix int to be lejos int
        return 0;
//        return image.getPixelReader().getColor((int) point.x, (int) point.y);
    }

    public static double getHeight() {
        return image.getHeight();
    }

    public static double getWidth() {
        return image.getWidth();
    }
}