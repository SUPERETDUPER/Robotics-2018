/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */


package ev3.navigation;

import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Taken from the lejos source code and modified to fix several bugs and remove useless stuff
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MyMovePilot implements MoveProvider {
    @NotNull
    private final Chassis chassis;
    @NotNull
    private final ArrayList<MoveListener> listeners = new ArrayList<>();
    private double minRadius;
    private volatile boolean moveActive = false;

    /**
     * Allocates a Pilot object.<br>
     *
     * @param chassis A Chassis object describing the physical parameters of the robot.
     */
    MyMovePilot(@NotNull Chassis chassis) {
        this.chassis = chassis;
        minRadius = chassis.getMinRadius();
        new Monitor().start();
    }

    public void addMoveListener(@NotNull MoveListener listener) {
        listeners.add(listener);
    }

    @NotNull
    public Chassis getChassis() {
        return chassis;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(double radius) {
        minRadius = radius;
    }

    /**
     * Added method to simplify work for Navigator
     */
    public void move(Move move, boolean immediateReturn) {
        switch (move.getMoveType()) {
            case TRAVEL:
                travel(move.getDistanceTraveled(), immediateReturn);
                break;
            case ROTATE:
                rotate(move.getAngleTurned(), immediateReturn);
                break;
            case ARC:
                arc(move.getArcRadius(), move.getAngleTurned(), immediateReturn);
                break;
            case STOP:
                stop();
                break;
        }
    }

    public void forward() {
        travel(Double.POSITIVE_INFINITY, true);
    }


    public void backward() {
        travel(Double.NEGATIVE_INFINITY, true);
    }


    public void travel(double distance) {
        travel(distance, false);

    }

    public void travel(double distance, boolean immediateReturn) {
        if (isMoving()) stop();

        Move move = new Move(
                Move.MoveType.TRAVEL,
                (float) distance, 0, (float) chassis.getLinearSpeed(), (float) chassis.getAngularSpeed(), chassis.isMoving()
        );

        chassis.moveStart();
        chassis.travel(distance);
        notifyMoveStart(move);
        if (!immediateReturn) waitForStop();
    }

    // Moves of the Arc family

    public void arcForward(double radius) {
        arc(radius, Double.POSITIVE_INFINITY, true);
    }


    public void arcBackward(double radius) {
        arc(radius, Double.NEGATIVE_INFINITY, true);
    }


    public void arc(double radius, double angle) {
        arc(radius, angle, false);
    }

    @Deprecated
    public void travelArc(double radius, double distance) {
        travelArc(radius, distance, false);
    }

    @Deprecated
    public void travelArc(double radius, double distance, boolean immediateReturn) {
        arc(radius, distance / (2 * Math.PI), immediateReturn);
    }

    public void arc(double radius, double angle, boolean immediateReturn) {
        angle = Math.abs(angle); // ADDED ABSOLUTE VALUE TO FIX BUG WITH NAVIGATOR SENDING NEGATIVE ANGLE AND CHASSIS REACTING INCORRECTLY

        if (radius == 0) {
            rotate(angle, immediateReturn);
            return;
        }

        if (Math.abs(radius) < minRadius) {
            throw new RuntimeException("Turn radius too small.");
        }
        if (isMoving()) {
            stop();
        }

        chassis.moveStart();
        chassis.arc(radius, angle);
        notifyMoveStart(new Move(
                Move.MoveType.ARC,
                (float) (Math.toRadians(angle) * radius),
                (float) angle,
                (float) chassis.getLinearSpeed(),
                (float) chassis.getAngularSpeed(),
                chassis.isMoving()
        ));

        if (!immediateReturn) waitForStop();
    }

    public void rotate(double angle) {
        rotate(angle, false);
    }

    public void rotateLeft() {
        rotate(Double.POSITIVE_INFINITY, true);
    }

    public void rotateRight() {
        rotate(Double.NEGATIVE_INFINITY, true);
    }

    /**
     * Created custom rotate to avoid the modified arc method
     */
    public void rotate(double angle, boolean immediateReturn) {
        if (minRadius != 0) {
            throw new RuntimeException("Turn radius too small.");
        }

        if (isMoving()) stop();

        chassis.moveStart();
        chassis.rotate(angle);
        notifyMoveStart(
                new Move(
                        Move.MoveType.ROTATE,
                        0,
                        (float) angle,
                        (float) chassis.getLinearSpeed(),
                        (float) chassis.getAngularSpeed(),
                        chassis.isMoving()
                )
        );

        if (!immediateReturn) waitForStop();
    }

    private void waitForStop() {
        while (moveActive) Thread.yield();
    }

    public void stop() {
        chassis.stop();
        waitForStop();
    }

    public synchronized boolean isMoving() {
        return moveActive;
    }

    // Methods dealing the start and end of a move

    private synchronized void notifyMoveStart(Move move) {
        moveActive = true;

        for (MoveListener ml : listeners) ml.moveStarted(move, this);
    }

    private synchronized void notifyStop() {
        moveActive = false;

        for (MoveListener ml : listeners) ml.moveStopped(chassis.getDisplacement(new Move(0, 0, false)), this);
    }

    public synchronized Move getMovement() {
        if (moveActive) {
            return chassis.getDisplacement(new Move(0, 0, true));
        } else {
            return new Move(Move.MoveType.STOP, 0, 0, false);
        }
    }


    /**
     * The monitor class detects end-of-move situations when non blocking move
     * call were made and makes sure these are dealt with.
     */
    private class Monitor extends Thread {
        Monitor() {
            setDaemon(true);
        }

        public void run() {
            //noinspection InfiniteLoopStatement
            for (; ; Thread.yield()) {
                if (isMoving()) {
                    if (chassis.isStalled()) MyMovePilot.this.stop();
                    if (!chassis.isMoving()) notifyStop();
                }
            }
        }
    }
}
