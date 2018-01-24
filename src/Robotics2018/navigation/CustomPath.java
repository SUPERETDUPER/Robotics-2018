package Robotics2018.navigation;


import Robotics2018.PC.GUI.Displayable;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

import java.awt.*;

public class CustomPath extends Path implements Displayable{
    @Override
    public void displayOnGUI(Graphics g) {
        Waypoint start = this.get(0);

        for (int i = 1; i < this.size() ; i++){
            g.drawLine((int) start.x, (int) start.y, (int) this.get(i).x, (int) this.get(i).y);
            start = this.get(i);
        }
    }
}
