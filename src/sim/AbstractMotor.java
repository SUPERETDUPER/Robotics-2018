package sim;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class AbstractMotor implements RegulatedMotor {
    private State currentState = State.STOPPED;
    private RegulatedMotorListener listener;
    private int speed = 100;
    private int currentSpeed = 100;
    private int timeToEnd;
    private int tachoCount;
    private int acceleration;

    public AbstractMotor() {
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

    @Override
    public void stop(boolean b) {
        currentState = State.STOPPED;
        if (listener != null) {
            listener.rotationStopped(this, 0, false, System.currentTimeMillis());
        }
    }

    @Override
    public void flt(boolean b) {
        currentState = State.FLOATING;
    }

    @Override
    public void waitComplete() {
        try {
            Thread.sleep(timeToEnd * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rotate(int i, boolean b) {
        timeToEnd = i / this.speed;
        if (!b) {
            waitComplete();
        }
    }

    @Override
    public void rotate(int i) {
        rotate(i, true);
    }

    @Override
    public void rotateTo(int i) {

    }

    @Override
    public void rotateTo(int i, boolean b) {

    }

    @Override
    public int getLimitAngle() {
        return 0;
    }

    @Override
    public int getSpeed() {
        return this.speed;
    }

    @Override
    public void setSpeed(int i) {
        this.speed = i;
    }

    @Override
    public float getMaxSpeed() {
        //TODO
        return 0;
    }

    @Override
    public boolean isStalled() {
        return currentState == State.STALLED;
    }

    @Override
    public void setStallThreshold(int i, int i1) {

    }

    @Override
    public void setAcceleration(int i) {
        this.acceleration = i;
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
    public void forward() {
        currentState = State.FORWARD;
    }

    @Override
    public void backward() {
        currentState = State.BACKWARD;
    }

    @Override
    public void stop() {
        stop(true);
    }

    @Override
    public void flt() {
        currentState = State.FLOATING;
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public int getRotationSpeed() {
        return this.currentSpeed;
    }

    @Override
    public int getTachoCount() {
        return this.tachoCount;
    }

    @Override
    public void resetTachoCount() {
        tachoCount = 0;
    }

    enum State {
        FORWARD,
        BACKWARD,
        STOPPED,
        FLOATING,
        STALLED
    }
}


