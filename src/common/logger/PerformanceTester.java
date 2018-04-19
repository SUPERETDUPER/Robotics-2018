/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.logger;

public class PerformanceTester {
    private static final String LOG_TAG = PerformanceTester.class.getSimpleName();

    private static long lastMarkTime = System.currentTimeMillis();

    private static StringBuilder messageBuilder = new StringBuilder();

    public static void mark(String message){
        long currentTime = System.currentTimeMillis();
        messageBuilder.append(message).append(": ").append(currentTime - lastMarkTime).append("\n");
        lastMarkTime = currentTime;
    }

    public static void send(){
        Logger.debug(LOG_TAG, messageBuilder.toString());
        messageBuilder = new StringBuilder();
    }
}
