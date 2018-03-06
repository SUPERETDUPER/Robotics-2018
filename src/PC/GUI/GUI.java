/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC.GUI;

import Common.Config;
import Common.Particles.ParticleData;
import Common.mapping.RegionSurfaceMap;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//TODO Add event listener for panel closing
public final class GUI {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final JFrame window = new JFrame();

    private static final DisplayablePath path = new DisplayablePath();
    private static final ParticleData mclData = new ParticleData();

    private static final List<Displayable> contents = new ArrayList<>(Arrays.asList(
            RegionSurfaceMap.get(),
            mclData,
            path
    ));

    private static final JComponent mainComponent = new JComponent() {
        @Override
        protected synchronized void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            super.paintComponent(g);

            g2d.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

//            Logger.debug(LOG_TAG, "Drawing GUI...");
            for (Displayable layer : contents) {
                layer.displayOnGui(g2d);
            }
        }
    };

    static {
        window.getContentPane().add(mainComponent);
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public static void updateMCLData(@NotNull DataInputStream dis) throws IOException {
        mclData.loadObject(dis);
        window.repaint();
    }

    public static void updatePaths(@NotNull DataInputStream dis) throws IOException {
        path.loadObject(dis);
        window.repaint();
    }

    static Pose getCurrentPose() {
        return mclData.getCurrentPose();
    }

    public static void close() {
        window.dispose();
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }
}