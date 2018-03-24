/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.MoveListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Taken from the Lejos Source code. Modified isMoving() to fix bug of listener not being called.
 */
@SuppressWarnings("ALL")
public class MyMovePilot implements ArcRotateMoveController {
    private double minRadius;
    private final Chassis chassis;
    private ArrayList<MoveListener> _listeners;
    private double linearSpeed;
    private double linearAcceleration;
    private double angularAcceleration;
    private double angularSpeed;
    private MyMovePilot.Monitor _monitor;
    private boolean _moveActive;
    private Move move;
    private boolean _replaceMove;

    /**
     * @deprecated
     */
    @Deprecated
    public MyMovePilot(double wheelDiameter, double trackWidth, RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
        this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public MyMovePilot(double wheelDiameter, double trackWidth, RegulatedMotor leftMotor, RegulatedMotor rightMotor, boolean reverse) {
        this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public MyMovePilot(double leftWheelDiameter, double rightWheelDiameter, double trackWidth, RegulatedMotor leftMotor, RegulatedMotor rightMotor, boolean reverse) {
        this(new WheeledChassis(new Wheel[]{WheeledChassis.modelWheel(leftMotor, leftWheelDiameter).offset(trackWidth / 2.0D).invert(reverse), WheeledChassis.modelWheel(rightMotor, rightWheelDiameter).offset(-trackWidth / 2.0D).invert(reverse)}, 2));
    }

    public MyMovePilot(Chassis chassis) {
        this.minRadius = 0.0D;
        this._listeners = new ArrayList();
        this._moveActive = false;
        this.move = null;
        this._replaceMove = false;
        this.chassis = chassis;
        this.linearSpeed = chassis.getMaxLinearSpeed() * 0.8D;
        this.angularSpeed = chassis.getMaxAngularSpeed() * 0.8D;
        chassis.setSpeed(this.linearSpeed, this.angularSpeed);
        this.linearAcceleration = this.getLinearSpeed() * 4.0D;
        this.angularAcceleration = this.getAngularSpeed() * 4.0D;
        chassis.setAcceleration(this.linearAcceleration, this.angularAcceleration);
        this.minRadius = chassis.getMinRadius();
        this._monitor = new MyMovePilot.Monitor();
        this._monitor.start();
    }

    public void setLinearAcceleration(double acceleration) {
        this.linearAcceleration = acceleration;
        this.chassis.setAcceleration(this.linearAcceleration, this.angularAcceleration);
    }

    public double getLinearAcceleration() {
        return this.linearAcceleration;
    }

    public void setAngularAcceleration(double acceleration) {
        this.angularAcceleration = acceleration;
        this.chassis.setAcceleration(this.linearAcceleration, this.angularAcceleration);
    }

    public double getAngularAcceleration() {
        return this.angularAcceleration;
    }

    public void setLinearSpeed(double speed) {
        this.linearSpeed = speed;
        this.chassis.setSpeed(this.linearSpeed, this.angularSpeed);
    }

    public double getLinearSpeed() {
        return this.linearSpeed;
    }

    public double getMaxLinearSpeed() {
        return this.chassis.getMaxLinearSpeed();
    }

    public void setAngularSpeed(double speed) {
        this.angularSpeed = speed;
        this.chassis.setSpeed(this.linearSpeed, this.angularSpeed);
    }

    public double getAngularSpeed() {
        return this.angularSpeed;
    }

    public double getMaxAngularSpeed() {
        return this.chassis.getMaxAngularSpeed();
    }

    public double getMinRadius() {
        return this.minRadius;
    }

    public void setMinRadius(double radius) {
        this.minRadius = radius;
    }

    public void forward() {
        this.travel(1.0D / 0.0, true);
    }

    public void backward() {
        this.travel(-1.0D / 0.0, true);
    }

    public void travel(double distance) {
        this.travel(distance, false);
    }

    public void travel(double distance, boolean immediateReturn) {
        if (this.chassis.isMoving()) {
            this.stop();
        }

        this.move = new Move(MoveType.TRAVEL, (float) distance, 0.0F, (float) this.linearSpeed, (float) this.angularSpeed, this.chassis.isMoving());
        this.chassis.moveStart();
        this.chassis.travel(distance);
        this.movementStart(immediateReturn);
    }

    public void arcForward(double radius) {
        this.arc(radius, 1.0D / 0.0, true);
    }

    public void arcBackward(double radius) {
        this.arc(radius, -1.0D / 0.0, true);
    }

    public void arc(double radius, double angle) {
        this.arc(radius, angle, false);
    }

    public void travelArc(double radius, double distance) {
        this.travelArc(radius, distance, false);
    }

    public void travelArc(double radius, double distance, boolean immediateReturn) {
        this.arc(radius, distance / 6.283185307179586D, immediateReturn);
    }

    public void rotate(double angle) {
        this.rotate(angle, false);
    }

    public void rotate(double angle, boolean immediateReturn) {
        this.arc(0.0D, angle, immediateReturn);
    }

    public void rotateLeft() {
        this.rotate(1.0D / 0.0, true);
    }

    public void rotateRight() {
        this.rotate(-1.0D / 0.0, true);
    }

    public void arc(double radius, double angle, boolean immediateReturn) {
        if (Math.abs(radius) < this.minRadius) {
            throw new RuntimeException("Turn radius too small.");
        } else {
            if (this._moveActive) {
                this.stop();
            }

            if (radius == 0.0D) {
                this.move = new Move(MoveType.ROTATE, 0.0F, (float) angle, (float) this.linearSpeed, (float) this.angularSpeed, this.chassis.isMoving());
            } else {
                this.move = new Move(MoveType.ARC, (float) (Math.toRadians(angle) * radius), (float) angle, (float) this.linearSpeed, (float) this.angularSpeed, this.chassis.isMoving());
            }

            this.chassis.moveStart();
            this.chassis.arc(radius, angle);
            this.movementStart(immediateReturn);
        }
    }

    public void stop() {
        this.chassis.stop();

        while (this._moveActive) {
            Thread.yield();
        }

    }

    public boolean isMoving() {
        return this._moveActive;
    }

    private void movementStart(boolean immediateReturn) {
        Iterator var2 = this._listeners.iterator();

        while (var2.hasNext()) {
            MoveListener ml = (MoveListener) var2.next();
            ml.moveStarted(this.move, this);
        }

        this._moveActive = true;
        MyMovePilot.Monitor var6 = this._monitor;
        synchronized (this._monitor) {
            this._monitor.notifyAll();
        }

        if (!immediateReturn) {
            while (this._moveActive) {
                Thread.yield();
            }

        }
    }

    private void movementStop() {
        if (!this._listeners.isEmpty()) {
            this.chassis.getDisplacement(this.move);
            Iterator var1 = this._listeners.iterator();

            while (var1.hasNext()) {
                MoveListener ml = (MoveListener) var1.next();
                ml.moveStopped(this.move, this);
            }
        }

        this._moveActive = false;
    }

    public Move getMovement() {
        Move result = this._moveActive ? this.chassis.getDisplacement(this.move) : new Move(MoveType.STOP, 0.0F, 0.0F, false);

        if (result.getMoveType() == MoveType.ARC) {
            throw new RuntimeException(result.toString() + "\n" + this.move.toString());
        }

        return result;
    }

    public void addMoveListener(MoveListener listener) {
        this._listeners.add(listener);
    }

    private class Monitor extends Thread {
        public boolean more = true;

        public Monitor() {
            this.setDaemon(true);
        }

        public synchronized void run() {
            while (this.more) {
                if (MyMovePilot.this._moveActive) {
                    if (MyMovePilot.this.chassis.isStalled()) {
                        MyMovePilot.this.stop();
                    }

                    if (!MyMovePilot.this.chassis.isMoving() || MyMovePilot.this._replaceMove) {
                        MyMovePilot.this.movementStop();
                        MyMovePilot.this._moveActive = false;
                        MyMovePilot.this._replaceMove = false;
                    }
                }

                try {
                    this.wait(MyMovePilot.this._moveActive ? 1L : 100L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            }

        }
    }
}
