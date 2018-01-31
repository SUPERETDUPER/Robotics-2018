package PC.GUI;

import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

import java.awt.*;

public class DisplayablePath extends Path implements Displayable {


    public void displayOnGui(Graphics g) {
        Waypoint previous = new Waypoint(GUI.getCurrentPose());

        g.setColor(Color.RED);

        for (Waypoint waypoint : this) {
            g.drawLine((int) previous.x, (int) previous.y, (int) waypoint.x, (int) waypoint.y);
            previous = waypoint;
        }
    }
}
