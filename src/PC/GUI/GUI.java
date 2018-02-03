package PC.GUI;

import Common.Config;
import Common.MCL.MCLData;
import Common.mapping.SurfaceMap;
import Common.utils.Logger;
import lejos.robotics.navigation.Pose;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final JFrame window = new JFrame();

    private static final DisplayablePath path = new DisplayablePath();
    private static final MCLData mclData = new MCLData();

    private static final ArrayList<Displayable> contents = new ArrayList<>(Arrays.asList(
            SurfaceMap.get(),
            mclData,
            path
    ));

    private static final JComponent mainComponent = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
            Logger.info(LOG_TAG, "Drawing GUI...");
            Graphics2D g2d = (Graphics2D) g;

            super.paintComponent(g2d);

            g2d.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

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

    public static void updateMCLData(DataInputStream dis) throws IOException {
        mclData.loadObject(dis);
        window.repaint();
    }

    public static void updatePaths(DataInputStream dis) throws IOException {
        path.loadObject(dis);
        window.repaint();
    }

    public static Pose getCurrentPose() {
        return mclData.getCurrentPose();
    }

    public static void close() {
        window.dispose();
    }

    public static void init() {
    }
}