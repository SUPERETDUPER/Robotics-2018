/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.mapping;

import Common.Config;
import Common.Logger;

import lejos.robotics.geometry.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;

public class ImageGenerator {
    private static final String LOG_TAG = ImageGenerator.class.getSimpleName();

    private static final Color LEJOS_BLUE = new java.awt.Color(0, 117, 191);
    private static final Color LEJOS_GREEN = new java.awt.Color(0, 172, 70);
    private static final Color LEJOS_RED = new java.awt.Color(237, 28, 36);
    private static final Color LEJOS_YELLOW = new java.awt.Color(255, 205, 3);

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private static final java.awt.Rectangle boundingRectangle = new java.awt.Rectangle(0, 0, 2362, 1143);

    private static final ArrayList<ColoredRegion> regions = new ArrayList<>();

    static {
        regions.add(new Rectangle(LEJOS_BLUE, 0, 0, 412.5F, 1143));

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
        regions.add(new Rectangle(LEJOS_YELLOW, 1530.5F, 274, 80, 64));
        regions.add(new Rectangle(LEJOS_BLUE, 1853, 274, 80, 64));
        regions.add(new Rectangle(LEJOS_RED, 1530.5F, 807, 80, 64));
        regions.add(new Rectangle(LEJOS_GREEN, 1853, 807, 80, 64));

        //TODO Make more precise, slightly off
        //Container lines
        regions.add(new Polygon(Color.BLACK, Arrays.asList(
                new Point(1328.4F, 272),
                new Point(1342.5F, 286.1F),
                new Point(841.5F, 797.1F),
                new Point(827.4F, 783)
        )));

        regions.add(new Polygon(Color.BLACK, Arrays.asList(
                new Point(896.4F, 431),
                new Point(910.5F, 416.9F),
                new Point(1207.6F, 732),
                new Point(1193.5F, 746.1F)
        )));

        //Containers base
        regions.add(new Rectangle(Color.WHITE, 822.5F, 343, 88, 88)); //Top-left
        regions.add(new Rectangle(Color.WHITE, 1254.5F, 272, 88, 88)); //Top-right
        regions.add(new Rectangle(Color.WHITE, 753.5F, 783, 88, 88)); //Bottom-left
        regions.add(new Rectangle(Color.WHITE, 1193.5F, 732, 88, 88)); //Bottom-right

        //Starting area
        regions.add(new Rectangle(LEJOS_GREEN, 2102, 435, 260, 270));
        regions.add(new Rectangle(Color.WHITE, 2112, 445, 250, 250));

        //Boats
        for (int i = 0; i < 6; i++) {
            regions.add(new Rectangle(Color.WHITE, 212.5F, 73 + 182.6F * i, 116, 84));
            regions.add(new Rectangle(Color.BLACK, 328.5F, 105 + 182.6F * i, 84, 20));
        }
    }

    private static java.awt.Color getDisplayColor(Point point){
        java.awt.Color colorUnderPoint = DEFAULT_COLOR;

        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                colorUnderPoint = region.getDisplayColor(point);
            }
        }

        return colorUnderPoint;
    }

    public static void main(String[] args) {
        BufferedImage image = new BufferedImage((int) boundingRectangle.getWidth(), (int) boundingRectangle.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < boundingRectangle.getWidth(); x++) {
            for (int y = 0; y < boundingRectangle.getHeight(); y++) {
                image.setRGB(x, y, getDisplayColor(new Point(x, y)).getRGB());
            }
        }

        try {
            File file = new File(Config.IMAGE_PATH);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to write Image to file" + e);
        }

    }
}
