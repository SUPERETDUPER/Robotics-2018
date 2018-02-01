package EV3.sim;

import Common.utils.Logger;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class AbstractMotor implements RegulatedMotor {
    private static final String LOG_TAG = AbstractMotor.class.getSimpleName();

    private final static int MAX_SPEED = 1050;
    private final static int DEFAULT_SPEED = 360;
    private final static int SPEED_REDUCER = 2;

    private final String name;

    private int tachoCount = 0;
    private int speed = DEFAULT_SPEED;


    private volatile boolean isMoving = false;
    private long timeStarted;
    private int rotateAmount;


    public AbstractMotor(String name) {
        this.name = name;
    }

    private synchronized void update() {
        if (!isMoving) {
            //Logger.debug(LOG_TAG, name + " : Not moving nothing to debug");
            return;
        }

        final long rotationsTraveled = (System.currentTimeMillis() - timeStarted) * speed / 1000; //distance = speed * time

        if (rotationsTraveled >= Math.abs(rotateAmount)) {
            isMoving = false;
            tachoCount += rotateAmount;
            Logger.debug(LOG_TAG, name + " : Done Move motor by " + rotationsTraveled);
        } else {
            //Logger.debug(LOG_TAG, name + " : Still moving " + rotationsTraveled + "/" + Math.abs(rotateAmount));
        }
    }

    @Override
    public synchronized void stop(boolean b) {
        update();

        if (isMoving) {
            isMoving = false;
            Logger.debug(LOG_TAG, name + " : Motor stopped");
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
    public synchronized void rotateTo(int i, boolean b) {
        rotate(i - getTachoCount(), b);
    }

    @Override
    public synchronized void rotate(int i, boolean b) {
        update();

        rotateAmount = i;
        isMoving = true;
        timeStarted = System.currentTimeMillis();

        Logger.debug(LOG_TAG, name + " : Moving motor by " + i + "...");

        if (!b) {
            waitComplete();
        }
    }

    @Override
    public synchronized int getLimitAngle() {
        update();

        if (isMoving) {
            Logger.warning(LOG_TAG, name + "Tried to getChassis limit angle but not moving");
        }

        return tachoCount + rotateAmount;
    }

    @Override
    public synchronized boolean isMoving() {
        update();
        return isMoving;
    }

    @Override
    public synchronized int getTachoCount() {
        update();
        return this.tachoCount;
    }

    @Override
    public synchronized void resetTachoCount() {
        update();
        tachoCount = 0;
    }

    @Override
    public synchronized int getRotationSpeed() {
        update();
        return isMoving ? speed : 0; //Ternary operator
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public void forward() {
        rotateTo(Integer.MAX_VALUE, true);
    }

    @Override
    public void backward() {
        rotateTo(Integer.MIN_VALUE, true);
    }

    @Override
    public int getSpeed() {
        return this.speed;
    }

    @Override
    public synchronized void setSpeed(int i) {
        update();
        //Logger.info(LOG_TAG, name + " : Set speed to " + i/10);
        this.speed = i / SPEED_REDUCER;
    }

    @Override
    public void rotateTo(int i) {
        rotateTo(i, true);
    }

    @Override
    public void rotate(int i) {
        rotate(i, true);
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
        //Logger.warning(LOG_TAG, "Acceleration not implemented");
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
        Logger.warning(LOG_TAG, "Did not implement callback thread for listener");
    }

    @Override
    public RegulatedMotorListener removeListener() {
        Logger.warning(LOG_TAG, "Did not implement callback thread for listener");
        return null;
    }
}


