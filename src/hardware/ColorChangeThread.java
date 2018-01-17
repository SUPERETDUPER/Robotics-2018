package hardware;

public class ColorChangeThread extends Thread {

    private int previousColor;

    public ColorChangeThread(){
        super();

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    @Override
    public void run() {
        super.run();

        int currentColor = ColorSensor.getSurfaceColor();

        if(previousColor != currentColor){
            // TODO : Update pose provider
            previousColor = currentColor;
        }
    }
}
