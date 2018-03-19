/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import Common.Config;
import Common.Logger;
import PC.GUI.Displayable;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SurfaceMap implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final SurfaceMap mSurfaceMap = new SurfaceMap();

    private BufferedImage image;

    private SurfaceMap() {
        try{
            image = ImageIO.read(new File(Config.IMAGE_PATH));
        } catch (IOException e){
            Logger.error(LOG_TAG, "Could not read map from file");
        }
    }

    @NotNull
    @Contract(pure = true)
    public static SurfaceMap get() {
        return mSurfaceMap;
    }

    public boolean contains(@NotNull Point point) {
        return point.x >= 0 && point.x < image.getWidth() && point.y >= 0 && point.y < image.getHeight();
    }

    @Override
    public void displayOnGui(@NotNull Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public int getColorAtPoint(Point point) {
        //TODO Fix int to be lejos int
        return image.getRGB((int) point.x, (int) point.y);
    }
}