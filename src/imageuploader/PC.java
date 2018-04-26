/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageuploader;

import common.Config;
import common.ConnectionUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Uploads an image to the EV3
 * Not pretty but works
 */
class PC {
    // --Commented out by Inspection (25/04/18 8:38 PM):private static final String LOG_TAG = PC.class.getSimpleName();

    public static void main(String[] args) {
        try {
            Files.copy(
                    new File(Config.DATA_PC_PATH).toPath(),
                    ConnectionUtil.createOutputStream(
                            ConnectionUtil.createServerSocket(Config.PORT_TO_CONNECT_ON_EV3)
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
