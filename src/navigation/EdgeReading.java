package navigation;

public class EdgeReading implements Reading {
    private final int previousColor;
    private final int currentColor;

    public EdgeReading(int previousColor, int currentColor) {
        this.previousColor = previousColor;
        this.currentColor = currentColor;
    }

    @Override
    public float calculateWeight(Particle particle) {
        //TODO :
        return 0;
    }
}
