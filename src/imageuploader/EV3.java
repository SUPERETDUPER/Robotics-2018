/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageuploader;

import common.Config;
import lejos.hardware.Button;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Uploads an image to the EV3
 * Not pretty but works
 */
class EV3 {
    private static final String LOG_TAG = EV3.class.getSimpleName();

    public static void main(String[] args) {
        List<String> data = null;
        try {
            data = getBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data == null) {
            return;
        }

        File file = new File(Config.DATA_EV3_PATH);
        System.out.println("Create file");

        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create new file");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("Failed to createImageOutputStream");
        }

        Button.ENTER.waitForPress();
    }

    private static List<String> getBufferedImage() throws IOException {
        List<String> data = new ArrayList<>();


        System.out.println("Waiting connection");
        InputStream inputStream = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        System.out.println("Connected. Reading Img");

        try {
            while (true) {
                data.add(reader.readLine());
            }
        } catch (IOException e) {
            System.out.println("Got Image");
        }


        return data;
    }
}
