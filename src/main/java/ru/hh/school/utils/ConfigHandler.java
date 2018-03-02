package ru.hh.school.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class ConfigHandler {
    public final static String RESOURCE_PATH = "resource_path";
    public final static String CACHE = "cache";

    private static Properties instance;

    public static void load() throws IOException {
        ClassLoader classLoader = ConfigHandler.class.getClassLoader();

        Properties properties = new Properties();
        try (InputStream defaultInputStream = classLoader.getResourceAsStream("config.properties")) {
            properties.load(defaultInputStream);
        }
        instance = properties;
    }

    public static Optional<String> getProperty(String propertyName){
        try{
            return Optional.ofNullable(instance.getProperty(propertyName));
        } catch (NullPointerException npe) {
            throw new IllegalStateException("Need to call ConfigHandler.load() first", npe);
        }
    }

    private ConfigHandler() {
    }
}
