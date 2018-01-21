package PC;

import geometry.SurfaceMap;
import navigation.MyPoseProvider;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI {

    private static final JFrame window;


    //List of layers to display on GUI
    private static final ArrayList<Displayable> contents = new ArrayList<>(Arrays.asList(
            SurfaceMap.get(),
            MyPoseProvider.get(),
            MyPoseProvider.get().getParticleSet()
    ));

    private static final JComponent mapGui = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();

            g2d.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

            for (Displayable content : contents) {
                content.displayOnGUI(g2d);
            }
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