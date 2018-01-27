package PC.GUI;

import Common.Config;
import Common.mapping.SurfaceMap;
import Common.navigation.MCL.MCLData;
import Common.utils.Logger;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;

public class GUI extends JFrame {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    public static Path path;
    private static final MCLData mclData = new MCLData();

    private static final DisplayableList<Displayable> contents = new DisplayableList<>();
    private static final JComponent mainComponent = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
            Logger.info(LOG_TAG, "Drawing GUI...");
            Graphics2D g2d = (Graphics2D) g;

            super.paintComponent(g2d);

            g2d.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

            contents.displayOnGUI(g2d);
            if (path != null) {
                displayPathsOnGui(g2d, mclData.getCurrentPose(), path);
            }
        }
    };

    static {
        contents.add(SurfaceMap.get());
        contents.add(mclData);
    }

    public GUI() {
        super();
        this.getContentPane().add(mainComponent);
        this.setVisible(true);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public void updateMCLData(DataInputStream dis) throws IOException {
        mclData.loadObject(dis);
    }

    //TODO Make cleaner
    private static void displayPathsOnGui(Graphics g, Pose startingPose, Path path) {
        Waypoint start = new Waypoint(startingPose);

        for (int i = 0; i < path.size(); i++) {
            g.setColor(Color.RED);
            g.drawLine((int) start.x, (int) start.y, (int) path.get(i).x, (int) path.get(i).y);
            start = path.get(i);
        }
    }
}