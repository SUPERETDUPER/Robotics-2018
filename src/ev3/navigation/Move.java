/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

public class Move {
    public enum Type {
        TRAVEL,
        ROTATE,
        ARC
    }

    private final Type type;
    private final Integer angle;
    private final Integer distance;
    private final Integer radius;

    private Move(Type type, Integer angle, Integer distance, Integer radius) {
        this.type = type;
        this.angle = angle;
        this.distance = distance;
        this.radius = radius;
    }

    public Integer getAngle() {
        return angle;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getRadius() {
        return radius;
    }

    public Type getType() {
        return type;
    }

    public static Move rotate(int angle){
        return new Move(Type.ROTATE, angle, null, null);
    }

    public static Move travel(int distance){
        return new Move(Type.TRAVEL, null, distance, null);
    }

    public static Move arc(int angle, int radius){
        return new Move(Type.ARC, angle, null, radius);
    }
}
