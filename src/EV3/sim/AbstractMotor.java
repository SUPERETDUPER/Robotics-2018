package EV3.sim;

import Common.utils.Logger;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

//TODO Not working correctly taking too long for short distances
public class AbstractMotor implements RegulatedMotor {
    private static final String LOG_TAG = AbstractMotor.class.getSimpleName();

    private boolean isMoving = false;
    private long timeStamp = System.currentTimeMillis();
    private int tachoCount = 0;

    private final static int MAX_SPEED = 1050;

    private int speed = 100;
    private int goalTachoCount;

    private void update() {
        if (!isMoving) {
            return;
        }

        long newTime = System.currentTimeMillis();
        int rotationsTraveled = (int) (newTime - timeStamp) / 1000 * speed;
        boolean isForward = goalTachoCount > tachoCount;

        if (!isForward) {
            rotationsTraveled *= -1;
        }

        if ((isForward && tachoCount + rotationsTraveled >= goalTachoCount) ||
                (!isForward && tachoCount + rotationsTraveled <= goalTachoCount)) {
            isMoving = false;
            tachoCount = goalTachoCount;
            timeStamp = newTime;
        }


    }

    public AbstractMotor() {
    }

    @Override
    public void stop(boolean b) {
        update();
        if (isMoving) {
            isMoving = false;
            timeStamp = System.currentTimeMillis();
        }
    }

    @Override
    public void waitComplete() {
        while (isMoving) {
            update();
            Thread.yield();
        }
    }

    @Override
    public void rotateTo(int i, boolean b) {
        update();
        goalTachoCount = i;
        isMoving = true;
        timeStamp = System.currentTimeMillis();

        if (!b) {
            waitComplete();
        }
    }

    @Override
    public void rotate(int i, boolean b) {
        rotateTo(tachoCount + i, b);
    }

    @Override
    public int getLimitAngle() {
        update();
        Logger.warning(LOG_TAG, "Not tested might return when no limit exists");
        return goalTachoCount;
    }

    @Override
    public boolean isMoving() {
        update();
        return isMoving;
    }

    @Override
    public int getTachoCount() {
        update();
        return this.tachoCount;
    }

    @Override
    public int getSpeed() {
        return this.speed;
    }

    @Override
    public void setSpeed(int i) {
        update();
        this.speed = i;
    }

    @Override
    public void rotate(int i) {
        rotate(i, true);
    }

    @Override
    public void resetTachoCount() {
        tachoCount = 0;
    }

    @Override
    public boolean isStalled() {
        return !isMoving;
    }

    @Override
    public void rotateTo(int i) {
        rotateTo(i, true);
    }

    @Override
    public int getRotationSpeed() {
        update();
        if (!isMoving) {
            return 0;
        }
        return speed;
    }

    @Override
    public void flt(boolean b) {
        stop(b);
    }

    @Override
    public void stop() {
        stop(true);
    }

    @Override
    public void flt() {
        flt(true);
    }

    @Override
    public void setStallThreshold(int i, int i1) {
    }

    @Override
    public void setAcceleration(int i) {
        Logger.warning(LOG_TAG, "Acceleration not implemented");
    }

    @Override
    public void synchronizeWith(RegulatedMotor[] regulatedMotors) {

    }

    @Override
    public void startSynchronization() {

    }

    @Override
    public void endSynchronization() {

    }

    @Override
    public void close() {

    }

    @Override
    public float getMaxSpeed() {
        return MAX_SPEED;
    }

    @Override
    public void addListener(RegulatedMotorListener regulatedMotorListener) {
        Logger.warning(LOG_TAG, "Did not implement callback thread for listner");
    }

    @Override
    public RegulatedMotorListener removeListener() {
        Logger.warning(LOG_TAG, "Did not implement callback thread for listner");
        return null;
    }

    @Override
    public void forward() {
        rotateTo(Integer.MAX_VALUE, true);
    }

    @Override
    public void backward() {
        rotateTo(Integer.MIN_VALUE, true);
    }
}


