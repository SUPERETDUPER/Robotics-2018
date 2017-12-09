package PC;

import geometry.SurfaceMap;

import javax.swing.*;

public class Application {


    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.getContentPane().add(SurfaceMap.getSurfaceComponent());
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
