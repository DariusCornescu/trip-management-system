package com.darius.project.repository.Database;

import org.slf4j.*;
import java.io.*;
import java.util.*;

public class Config {
    private static final Properties props = new Properties();
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    static {
        try (InputStream input = Config.class.getResourceAsStream("/config.properties")) {
            props.load(input);
        } catch (IOException e) {
            LOGGER.error("Error loading properties file", e);
        }
    }
    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}