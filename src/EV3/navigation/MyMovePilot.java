//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package EV3.navigation;

import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.RotateMoveController;

import java.util.ArrayList;

public class MyMovePilot implements RotateMoveController {
    private final Chassis chassis;
    private final ArrayList<MoveListener> _listeners = new ArrayList<>();
    private final MyMovePilot.Monitor _monitor = new MyMovePilot.Monitor();
    private volatile boolean _moving = false;
    private Move move;

    public MyMovePilot(Chassis chassis) {
        this.chassis = chassis;
        this.chassis.setSpeed(chassis.getMaxLinearSpeed() * 0.8D, chassis.getMaxAngularSpeed() * 0.8D);
        this.chassis.setAcceleration(this.getLinearSpeed() * 4.0D, this.getAngularSpeed() * 4.0D);
        this._monitor.start();
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
        if (_moving) {
            this.stop();
        }

        this.move = new Move(MoveType.TRAVEL, (float) distance, 0.0F, (float) getLinearSpeed(), (float) getAngularSpeed(), _moving);
        this.chassis.moveStart();
        this.chassis.travel(distance);
        this.movementStart(immediateReturn);
    }

    public void rotate(double angle, boolean immediateReturn) {
        if (this._moving) {
            this.stop();
        }

        this.move = new Move(MoveType.ROTATE, 0.0F, (float) angle, (float) getLinearSpeed(), (float) getAngularSpeed(), _moving);
        this.chassis.moveStart();
        this.chassis.arc(0, angle);
        this.movementStart(immediateReturn);
    }

    public void stop() {
        this.chassis.stop();
        waitForStop();
    }

    private void waitForStop() {
        while (this._moving) {
            Thread.yield();
        }
    }

    public boolean isMoving() {
        return this._moving;
    }

    private void movementStart(boolean immediateReturn) {
        for (MoveListener ml : this._listeners) {
            ml.moveStarted(this.move, this);
        }

        this._moving = true;
        synchronized (this._monitor) {
            this._monitor.notifyAll();
        }

        if (!immediateReturn) {
            waitForStop();
        }
    }

    private void movementStop() {
        if (!this._listeners.isEmpty()) {
            this.move = this.chassis.getDisplacement(this.move);

            for (MoveListener ml : this._listeners) {
                ml.moveStopped(this.move, this);
            }
        }

        this._moving = false;
    }

    public Move getMovement() {
        return this._moving ? this.chassis.getDisplacement(this.move) : new Move(MoveType.STOP, 0.0F, 0.0F, false);
    }

    public void addMoveListener(MoveListener listener) {
        this._listeners.add(listener);
    }

    private class Monitor extends Thread {
        private Monitor() {
            this.setDaemon(true);
        }

        public synchronized void run() {
            while (true) {
                if (MyMovePilot.this._moving) {
                    if (MyMovePilot.this.chassis.isStalled()) {
                        MyMovePilot.this.stop();
                    }

                    if (!MyMovePilot.this.chassis.isMoving()) {
                        MyMovePilot.this.movementStop();
                        MyMovePilot.this._moving = false;
                    }
                }

                try {
                    this.wait(MyMovePilot.this._moving ? 1L : 100L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            }
        }
    }
}
