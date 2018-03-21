/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.navigation;

import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.RotateMoveController;
import lejos.utility.Delay;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Taken from the Lejos Source Code MovePilot class and modified to fix bug with Move STOPPED not getting called with navigator
 */
public class MyMovePilot implements RotateMoveController {
    private final Chassis chassis;
    private final ArrayList<MoveListener> _listeners = new ArrayList<>();
    private volatile boolean isMoving = false;
    private Move currentMove;

    public MyMovePilot(Chassis chassis) {
        this.chassis = chassis;

        new MyMovePilot.Monitor().start();
    }

    public void setLinearAcceleration(double acceleration) {
        this.chassis.setLinearAcceleration(acceleration);
    }

    public double getLinearAcceleration() {
        return this.chassis.getLinearAcceleration();
    }

    public void setAngularAcceleration(double acceleration) {
        this.chassis.setAngularAcceleration(acceleration);
    }

    public double getAngularAcceleration() {
        return this.chassis.getAngularAcceleration();
    }

    public void setLinearSpeed(double speed) {
        this.chassis.setLinearSpeed(speed);
    }

    public double getLinearSpeed() {
        return this.chassis.getLinearSpeed();
    }

    public double getMaxLinearSpeed() {
        return this.chassis.getMaxLinearSpeed();
    }

    public void setAngularSpeed(double speed) {
        this.chassis.setAngularSpeed(speed);
    }

    public double getAngularSpeed() {
        return this.chassis.getAngularSpeed();
    }

    public double getMaxAngularSpeed() {
        return this.chassis.getMaxAngularSpeed();
    }

    public void forward() {
        this.travel(Double.MAX_VALUE, true);
    }

    public void backward() {
        this.travel(Double.MIN_VALUE, true);
    }

    public void travel(double distance) {
        this.travel(distance, false);
    }

    public void rotate(double angle) {
        this.rotate(angle, false);
    }

    public void rotateLeft() {
        this.rotate(Double.MAX_VALUE, true);
    }

    public void rotateRight() {
        this.rotate(Double.MIN_VALUE, true);
    }

    public void travel(double distance, boolean immediateReturn) {
        if (isMoving) {
            this.stop();
        }

        this.currentMove = new Move(MoveType.TRAVEL, (float) distance, 0.0F, (float) getLinearSpeed(), (float) getAngularSpeed(), isMoving);
        this.chassis.moveStart();
        this.chassis.travel(distance);
        this.notifyMovementStart();
        this.isMoving = true;

        if (!immediateReturn) {
            waitForStop();
        }
    }

    public void rotate(double angle, boolean immediateReturn) {
        if (this.isMoving) {
            this.stop();
        }

        this.currentMove = new Move(MoveType.ROTATE, 0.0F, (float) angle, (float) getLinearSpeed(), (float) getAngularSpeed(), isMoving);
        this.chassis.moveStart();
        this.chassis.arc(0, angle);
        this.notifyMovementStart();
        this.isMoving = true;

        if (!immediateReturn) {
            waitForStop();
        }
    }

    public void stop() {
        this.chassis.stop();
        waitForStop();
    }

    private void waitForStop() {
        while (this.isMoving) {
            Thread.yield();
        }
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    private void notifyMovementStart() {
        for (MoveListener ml : this._listeners) {
            ml.moveStarted(this.currentMove, this);
        }
    }

    @NotNull
    public Move getMovement() {
        return this.isMoving ? this.chassis.getDisplacement(currentMove) : new Move(MoveType.STOP, 0.0F, 0.0F, false);
    }

    public void addMoveListener(MoveListener listener) {
        this._listeners.add(listener);
    }

    private class Monitor extends Thread {
        private Monitor() {
            this.setName("MovePilot");
            this.setDaemon(true);
        }

        public synchronized void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                for (; MyMovePilot.this.isMoving; Thread.yield()) {

                    if (MyMovePilot.this.chassis.isStalled()) {
                        MyMovePilot.this.chassis.stop();
                    }

                    if (!MyMovePilot.this.chassis.isMoving()) {
                        this.notifyMoveStop();
                        MyMovePilot.this.isMoving = false;
                    }
                }

                Delay.msDelay(100L);
            }
        }

        private void notifyMoveStop() {
            MyMovePilot.this.currentMove = MyMovePilot.this.chassis.getDisplacement(MyMovePilot.this.currentMove);

            for (MoveListener ml : MyMovePilot.this._listeners) {
                ml.moveStopped(MyMovePilot.this.currentMove, MyMovePilot.this);
            }
        }
    }
}
