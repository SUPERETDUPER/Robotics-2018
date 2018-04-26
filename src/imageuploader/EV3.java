/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageuploader;

import common.Config;
import common.ConnectionUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Uploads an image to the EV3
 * Not pretty but works
 */
class EV3 {
    public static void main(String[] args) {
        try {
            Files.copy(
                    ConnectionUtil.getInputStream(
                            ConnectionUtil.createServerSocket(Config.PORT_TO_CONNECT_ON_EV3)
                    ),
                    new File(Config.DATA_EV3_PATH).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
