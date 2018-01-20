package navigation;

import com.sun.istack.internal.NotNull;
import geometry.SurfaceMap;
import hardware.ColorSensor;

class Readings {

    private final int color;
    private final boolean onEdge;

    Readings(@NotNull boolean onEdge) {
        this.color = ColorSensor.getSurfaceColor();
        this.onEdge = onEdge;
    }

    @NotNull
    public float calculateWeight(@NotNull Particle particle) {
        if (!SurfaceMap.get().contains(particle.getPose().getLocation()) || SurfaceMap.get().colorAtPoint(particle.getPose().getLocation()) != color) {
            return 0;
        }
        return 1;
    }
}