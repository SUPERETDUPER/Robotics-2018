package sim;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class AbstractMotor implements RegulatedMotor {

    private RegulatedMotorListener listener;

    private final static int maxSpeed = 1050;
    private int tachoCount = 0;

    private int speed = 100;
    private int currentSpeed = 0;
    private long endTime = -1;
    private int angleAtEnd;

    public AbstractMotor() {
    }

    @Override
    public void stop(boolean b) {
        currentSpeed = 0;
        endTime = -1;
        if (listener != null) {
            listener.rotationStopped(this, tachoCount, true, System.currentTimeMillis());
        }
    }

    private void start(long endTime, boolean forward) {
        this.endTime = endTime;

        if (forward) {
            this.currentSpeed = speed;
        } else {
            this.currentSpeed = -speed;
        }

        if (listener != null) {
            listener.rotationStarted(this, tachoCount, false, System.currentTimeMillis());
        }
    }

    @Override
    public void waitComplete() {
        while (System.currentTimeMillis() < endTime) {
            Thread.yield();
        }
    }

    private void check() {
        if (System.currentTimeMillis() > endTime && endTime != -1) {
            stop(true);
        }
    }

    @Override
    public void flt(boolean b) {
        stop(b);
    }



    @Override
    public void rotate(int i, boolean b) {
        start((long) ((float) i / (float) this.currentSpeed) + System.currentTimeMillis(), i >= 0);

        if (!b) {
            waitComplete();
        }
    }


    @Override
    public void rotateTo(int i, boolean b) {
        angleAtEnd = i;
        rotate(i - tachoCount, b);
    }

    @Override
    public int getLimitAngle() {
        check();
        return angleAtEnd;
    }

    @Override
    public int getSpeed() {
        check();
        return this.speed;
    }

    @Override
    public void setSpeed(int i) {
        this.speed = i;
    }

    @Override
    public boolean isStalled() {
        check();
        return currentSpeed == 0;
    }

    @Override
    public void forward() {
        start(-1, true);
    }

    @Override
    public void backward() {
        currentSpeed = -speed;
        start(-1, false);

    }

    @Override
    public boolean isMoving() {
        check();
        return currentSpeed != 0;
    }

    @Override
    public int getTachoCount() {
        check();
        return this.tachoCount;
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
    public void rotateTo(int i) {
        rotateTo(i, true);
    }

    @Override
    public int getRotationSpeed() {
        return this.currentSpeed;
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
        return maxSpeed;
    }

    @Override
    public void addListener(RegulatedMotorListener regulatedMotorListener) {
        this.listener = regulatedMotorListener;
    }

    @Override
    public RegulatedMotorListener removeListener() {
        RegulatedMotorListener oldListener = listener;
        listener = null;
        return oldListener;
    }
}


