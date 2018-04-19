/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageGenerator;

import common.Config;
import common.logger.Logger;
import common.mapping.ColorJavaLejos;
import lejos.robotics.geometry.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generates the surface map image
 */
class ImageGenerator {
    private static final String LOG_TAG = ImageGenerator.class.getSimpleName();

    private static final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 2362, 1143);

    private static final ArrayList<ColorRegion> regions = new ArrayList<>();

    static {
        regions.add(new Rectangle(ColorJavaLejos.MAP_BLUE, 0, 0, 412.5F, 1143));

        //Vertical lines
        regions.add(new Rectangle(Color.BLACK, 180, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 412.5F, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 655.5F, 138, 20, 867));
        regions.add(new Rectangle(Color.BLACK, 1044.5F, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 1722.5F, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 2222, 0, 20, 1143));

        //Horizontal lines
        regions.add(new Rectangle(Color.BLACK, 432.5F, 566, 223, 20));
        regions.add(new Rectangle(Color.BLACK, 655.5F, 138, 1582, 20));
        regions.add(new Rectangle(Color.BLACK, 655.5F, 1005, 1582, 20));

        //Temp reg area lines
        regions.add(new Rectangle(Color.BLACK, 1610.5F, 294, 243, 20));
        regions.add(new Rectangle(Color.BLACK, 1610.5F, 828, 243, 20));

        //Temp reg area base
        regions.add(new Rectangle(ColorJavaLejos.MAP_YELLOW, 1530.5F, 274, 80, 64));
        regions.add(new Rectangle(ColorJavaLejos.MAP_BLUE, 1853, 274, 80, 64));
        regions.add(new Rectangle(ColorJavaLejos.MAP_RED, 1530.5F, 807, 80, 64));
        regions.add(new Rectangle(ColorJavaLejos.MAP_GREEN, 1853, 807, 80, 64));

        //Container lines
        regions.add(new IrregularPolygon(Color.BLACK, Arrays.asList(
                new Point(827, 766),
                new Point(842, 780),
                new Point(1274, 360),
                new Point(1260, 346)
//                corner of grey box 841.5 766 bot left
//                corner of grey box 1259.5 360 top right
        )));

        //                corner of grey box 915.5 431 top left
        //                corner of grey box 1191.5 695 bot right
        //                length of little black part = 10* square root (2) = 14.14
        regions.add(new IrregularPolygon(Color.BLACK, Arrays.asList(
                new Point(916, 417),
                new Point(901, 431),
                new Point(1192, 709),
                new Point(1206, 695)

        )));

        //Containers base
        regions.add(new Rectangle(Color.LIGHT_GRAY, 827.5F, 343, 88, 88)); //Top-left
        regions.add(new Rectangle(Color.LIGHT_GRAY, 1259.5F, 272, 88, 88)); //Top-right
        regions.add(new Rectangle(Color.LIGHT_GRAY, 753.5F, 766, 88, 88)); //Bottom-left
        regions.add(new Rectangle(Color.LIGHT_GRAY, 1191.5F, 695, 88, 88)); //Bottom-right

        //Containers base white small rectangle
        regions.add(new Rectangle(Color.WHITE, 847.5F, 363, 48, 48)); //Top-left
        regions.add(new Rectangle(Color.WHITE, 1279.5F, 292, 48, 48)); //Top-right
        regions.add(new Rectangle(Color.WHITE, 773.5F, 786, 48, 48)); //Bottom-left
        regions.add(new Rectangle(Color.WHITE, 1211.5F, 715, 48, 48)); //Bottom-right

        //Starting area
        regions.add(new Rectangle(ColorJavaLejos.MAP_GREEN, 2102, 435, 260, 270));
        regions.add(new Rectangle(Color.WHITE, 2112, 445, 250, 250));

        //Boats
        for (int i = 0; i < 6; i++) {
            regions.add(new Rectangle(Color.LIGHT_GRAY, 212.5F, 73 + 182.6F * i, 116, 84));
            regions.add(new Rectangle(Color.WHITE, 222.5F, 83 + 182.6F * i, 96, 64));
            regions.add(new Rectangle(Color.BLACK, 328.5F, 105 + 182.6F * i, 84, 20));
        }
    }

    private static Color getDisplayColor(float x, float y) {
        Color colorUnderPoint = boundingRectangle.getDisplayColor();

        for (ColorRegion region : ImageGenerator.regions) {
            if (region.contains(x, y)) {
                colorUnderPoint = region.getDisplayColor();
            }
        }

        return colorUnderPoint;
    }

    /**
     * Generates an image
     *
     * @param pathToSave where to save the image
     */
    @SuppressWarnings("SameParameterValue")
    private static void generateImage(String pathToSave) {
        BufferedImage image = new BufferedImage(
                (int) boundingRectangle.getWidth(),
                (int) boundingRectangle.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        //Loop through every pixel
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, getDisplayColor(x, y).getRGB());
            }
        }

        //Save Image
        try {
            File file = new File(pathToSave);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to write Image to file" + e);
        }
    }

    public static void main(String[] args) {
        generateImage(Config.PC_IMAGE_PATH);
    }
}