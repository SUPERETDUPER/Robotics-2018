/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import common.mapping.MapDataReader;
import ev3.navigation.NavigatorBuilder;
import ev3.robot.Robot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.localization.PoseProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simulated EV3 robot holding all the EV3 parts
 */
public class SimRobot implements Robot {
    private static final String LOG_TAG = SimRobot.class.getSimpleName();

    private final Chassis chassis = NavigatorBuilder.buildChassis(
            new SimMotor("leftMotor"),
            new SimMotor("rightMotor")
    );

    private final Paddle paddle = new SimPaddle();
    private final Arm arm = new SimArm();
    private final Brick brick = new SimBrick();

    private ColorSensors colorSensors;

    private PoseProvider poseProvider;
    @NotNull
    private final MapDataReader surfaceMap;

    public SimRobot(@NotNull MapDataReader surfaceMap) {
        this.surfaceMap = surfaceMap;
    }

    @Override
    public void setup() {
        Logger.info(LOG_TAG, "Done setting up robot");
    }

    @Override
    public boolean isSetup() {
        return true;
    }

    public void setPoseProvider(PoseProvider poseProvider) {
        this.poseProvider = poseProvider;
    }

    @Nullable
    @Override
    public ColorSensors getColorSensors() {
        if (colorSensors == null) {
            if (poseProvider == null) {
                return null;
            }

            colorSensors = new SimColorSensors(poseProvider, surfaceMap);
        }

        return colorSensors;
    }

    @Override
    public Arm getArm() {
        return arm;
    }

    @Override
    public Chassis getChassis() {
        return chassis;
    }

    @Override
    public Paddle getPaddle() {
        return paddle;
    }

    @Override
    public Brick getBrick() {
        return brick;
    }

    @Override
    public DistanceSensor getDistanceSensor() {
        return new DistanceSensor() {
            @Override
            public float getDistance() {
                return -1;
            }
        };
    }
}
