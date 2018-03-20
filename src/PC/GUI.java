/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.EventTypes;
import Common.Logger;
import Common.Particles.ParticleData;
import Common.mapping.SurfaceMap;
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
public final class GUI extends JFrame implements DataChangeListener {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private final DisplayablePath path = new DisplayablePath();
    private final ParticleData mclData = new ParticleData();
    private final static GUI mGui = new GUI();

    private final List<Displayable> contents = new ArrayList<>(Arrays.asList(
            new SurfaceMap(),
            mclData,
            path
    ));

    private final JComponent mainComponent = new JComponent() {
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

    public static GUI get() {
        return mGui;
    }

    private GUI() {
        super();
        Connection.setListener(this);
        this.getContentPane().add(mainComponent);
        this.setVisible(true);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    private void updateMCLData(@NotNull DataInputStream dis) throws IOException {
        mclData.loadObject(dis);
        this.repaint();
    }

    private void updatePaths(@NotNull DataInputStream dis) throws IOException {
        path.loadObject(dis);
        this.repaint();
    }

    Pose getCurrentPose() {
        return mclData.getCurrentPose();
    }

    @SuppressWarnings("EmptyMethod")
    static void init() {
    }

    @Override
    public void dataChanged(EventTypes event, DataInputStream dis) throws IOException {
        switch (event) {
            case MCL_DATA:
                updateMCLData(dis);
                break;
            case LOG:
                System.out.println(dis.readUTF());
                break;
            case PATH:
                updatePaths(dis);
                break;
            default:
                Logger.error(LOG_TAG, "Not a recognized event type");
        }
    }

    @Override
    public void connectionLost() {
        this.dispose();
    }
}