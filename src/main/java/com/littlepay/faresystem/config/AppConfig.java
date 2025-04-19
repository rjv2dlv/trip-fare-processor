package com.littlepay.faresystem.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class AppConfig {
    private static final Properties properties = new Properties();
    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if(input == null) {
                log.error("Unable to find the application.properties file");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
