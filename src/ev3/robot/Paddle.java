/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

public interface Paddle {
    void move(boolean immediateReturn);

    void hitBlock(boolean immediateReturn);
}
