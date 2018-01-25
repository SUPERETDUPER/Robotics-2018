package Robotics2018.PC.GUI;

import Robotics2018.mapping.SurfaceMap;
import Robotics2018.navigation.CustomPath;
import Robotics2018.navigation.MCL.MyPoseProvider;
import Robotics2018.Config;
import Robotics2018.utils.Logger;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final JFrame window;

    public static final DisplayableList<CustomPath> paths = new DisplayableList<>();

    private static final DisplayableList<Displayable> contents = new DisplayableList<>();

    static {
        contents.add(SurfaceMap.get());
        contents.add(MyPoseProvider.get());
        contents.add(paths);
    }


    private static final JComponent mapGui = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
            Logger.info(LOG_TAG, "Drawing GUI...");
            Graphics2D g2d = (Graphics2D) g;

            super.paintComponent(g2d);

            g2d.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

            contents.displayOnGUI(g2d);
        }
    };

    /*
    Creates a window and loads it's content
     */
    static {
        window = new JFrame();
        window.getContentPane().add(mapGui);
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public static void init() {
    }

    public static void update() {
        window.repaint();
    }

    public static void close() {
        window.dispose();
    }
}