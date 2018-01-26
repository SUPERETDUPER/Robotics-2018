package PC.GUI;

import Common.Config;
import Common.mapping.SurfaceMap;
import Common.navigation.CustomPath;
import Common.navigation.MCL.MCLData;
import Common.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;

public class GUI extends JFrame {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    public static final DisplayableList<CustomPath> paths = new DisplayableList<>();
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
        }
    };

    static {
        contents.add(SurfaceMap.get());
        contents.add(mclData);
        contents.add(paths);
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
}