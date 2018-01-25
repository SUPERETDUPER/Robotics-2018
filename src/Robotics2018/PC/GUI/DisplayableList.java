package Robotics2018.PC.GUI;

import java.awt.*;
import java.util.ArrayList;

public class DisplayableList<T extends Displayable> extends ArrayList<T> implements Displayable{


    @Override
    public void displayOnGUI(Graphics g) {
        for(T displayableObject: this){
            displayableObject.displayOnGUI(g);
        }
    }
}