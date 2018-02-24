/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package EV3.sim;

import Common.Config;
import Common.Logger;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import org.jetbrains.annotations.Nullable;

/**
 * Replicates the behaviour of an EV3 motor. Is used by the simulator
 */
public class AbstractMotor implements RegulatedMotor {
    private static final String LOG_TAG = AbstractMotor.class.getSimpleName();

    private final static int MAX_SPEED = 1050;
    private final static int DEFAULT_SPEED = 360;

    private final String name;

    private int currentTachoCount = 0;
    private int startTachoCount = 0;
    private int endTachoCount;

    private long timeAtStart;

    private int speed = DEFAULT_SPEED;

    public AbstractMotor(String name) {
        this.name = name;
    }

    private synchronized void update() {
        if (endTachoCount == currentTachoCount) {
            //Logger.debug(LOG_TAG, name + " : Not moving nothing to debug");
            return;
        }

        int rotationsTraveled = (int) (System.currentTimeMillis() - timeAtStart) * speed / 1000; //distance = speed * time

        rotationsTraveled = Math.min(rotationsTraveled, Math.abs(endTachoCount - startTachoCount)); //If rotated more then necessary reduce to cap

        if (endTachoCount < currentTachoCount) {
            rotationsTraveled *= -1;
        }

        currentTachoCount = startTachoCount + rotationsTraveled;
    }

    @Override
    public synchronized void stop(boolean b) {
        update();
        endTachoCount = currentTachoCount;
    }

    @Override
    public synchronized void waitComplete() {
        while (currentTachoCount != endTachoCount) {
            update();
            Thread.yield();
        }
    }

    @Override
    public synchronized void rotateTo(int i, boolean b) {
        update();

        startTachoCount = currentTachoCount;
        timeAtStart = System.currentTimeMillis();
        endTachoCount = i;

        if (!b) {
            waitComplete();
        }
    }

    @Override
    public synchronized int getLimitAngle() {
        update();

        if (endTachoCount == currentTachoCount) {
            Logger.warning(LOG_TAG, name + "Tried to getChassis limit angle but not moving");
        }

        return endTachoCount;
    }

    @Override
    public synchronized void rotate(int i, boolean b) {
        update();

        rotateTo(currentTachoCount + i, b);
    }

    @Override
    public synchronized boolean isMoving() {
        update();
        return endTachoCount != currentTachoCount;
    }

    @Override
    public synchronized int getTachoCount() {
        update();
        return this.currentTachoCount;
    }

    @Override
    public synchronized void resetTachoCount() {
        update();
        startTachoCount -= currentTachoCount;
        endTachoCount -= currentTachoCount;
        currentTachoCount = 0;
        Logger.warning(LOG_TAG, "Might not work with rotateTo method");
    }

    @Override
    public int getRotationSpeed() {
        update();
        return endTachoCount == currentTachoCount ? 0 : speed; //Ternary operator
    }

    @Override
    public void setSpeed(int i) {
        update();
        this.speed = i / Config.SIM_SPEED_REDUCING_FACTOR;
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

    @Nullable
    @Override
    public RegulatedMotorListener removeListener() {
        Logger.warning(LOG_TAG, "Did not implement callback thread for listener");
        return null;
    }
}
