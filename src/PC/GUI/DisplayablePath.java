package PC.GUI;

import Common.Logger;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class DisplayablePath extends Path implements Displayable {
    private static final String LOG_TAG = DisplayablePath.class.getSimpleName();

    public void displayOnGui(@NotNull Graphics g) {
        if (GUI.getCurrentPose() == null) {
            Logger.warning(LOG_TAG, "Could not display path, no current position");
            return;
        }

        Waypoint previous = new Waypoint(GUI.getCurrentPose());

        g.setColor(Color.RED);

        for (Waypoint waypoint : this) {
            g.drawLine((int) previous.x, (int) previous.y, (int) waypoint.x, (int) waypoint.y);
            previous = waypoint;
        }
    }
}
