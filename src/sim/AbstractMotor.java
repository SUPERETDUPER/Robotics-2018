package sim;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class AbstractMotor implements RegulatedMotor {

    private RegulatedMotorListener listener;

    private State currentState = State.STOP;
    private long timeStamp = -1;

    private final static int maxSpeed = 1050;
    private int tachoCount = 0;

    private int speed = 100;
    private int goalTachoCount;

    private void update() {
        if (currentState == State.STOP) {
            return;
        }

        long newTime = System.currentTimeMillis();
        int rotationsTraveled = (int) ((newTime - timeStamp) * speed);

        if (currentState == State.BACKWARD) {
            rotationsTraveled *= -1;
        }

        tachoCount += rotationsTraveled;

        if ((currentState == State.FORWARD && tachoCount > goalTachoCount) ||
                (currentState == State.BACKWARD && tachoCount < goalTachoCount)) {
            currentState = State.STOP;
            if (listener != null) {
                listener.rotationStopped(this, tachoCount, true, newTime);
            }
        }

        timeStamp = newTime;
    }

    public AbstractMotor() {
    }

    private float getTimeToEnd() {
        return Math.abs(goalTachoCount - tachoCount) / speed;
    }

    @Override
    public void stop(boolean b) {
        update();

        if (currentState != State.STOP) {
            currentState = State.STOP;
            timeStamp = System.currentTimeMillis();

            if (listener != null) {
                listener.rotationStopped(this, tachoCount, true, timeStamp);
            }
        }
    }

    @Override
    public void waitComplete() {
        while (System.currentTimeMillis() < getTimeToEnd()) {
            Thread.yield();
        }
    }

    @Override
    public void rotateTo(int i, boolean b) {
        goalTachoCount = i;
        goalTachoCount
    }

    @Override
    public void rotate(int i, boolean b) {
        start((long) ((float) i / (float) this.currentSpeed) + System.currentTimeMillis(), i >= 0);

        if (!b) {
            waitComplete();
        }
    }

    @Override
    public int getLimitAngle() {
        update();
        return tachoCount +;
    }

    @Override
    public void forward() {
        update();
        currentState = State.FORWARD;
        rota = Integer.MAX_VALUE;
        timeStamp = System.currentTimeMillis();
    }

    @Override
    public void backward() {
        update();
        currentState = State.BACKWARD;
        rota = Integer.MAX_VALUE;
        timeStamp = System.currentTimeMillis();
    }

    @Override
    public boolean isMoving() {
        update();
        return currentState != State.STOP;
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
        return !isMoving();
    }

    @Override
    public void rotateTo(int i) {
        rotateTo(i, true);
    }

    @Override
    public int getRotationSpeed() {
        if (currentState == State.STOP) {
            return 0;
        }
        return this.speed;
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

    private enum State {
        FORWARD,
        BACKWARD,
        STOP
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


