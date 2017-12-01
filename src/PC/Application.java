package PC;

import geometry.SurfaceMap;
import lejos.ev3.tools.*;
import utils.logger.Logger;

import javax.swing.JFrame;

public class Application {


    public static void main(String[] args) {

        JFrame window = new JFrame();
        window.setBounds(0,0, (int) SurfaceMap.BOUNDING_RECTANGLE.width, (int) SurfaceMap.BOUNDING_RECTANGLE.height);
        window.getContentPane().add(SurfaceMap.getDefaultSurfaceMap());
        window.setVisible(true);

        EV3MCLCommand command = new EV3MCLCommand();
        try {
            command.run();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
