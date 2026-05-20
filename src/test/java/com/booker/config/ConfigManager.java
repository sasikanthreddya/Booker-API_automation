package com.booker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties = new Properties();
    private static ConfigManager instance;

    private ConfigManager() {
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        String env = System.getProperty("env", "default");
        String configFile = env.equals("default") ? "config.properties" : "config-" + env + ".properties";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                // Fallback to default
                try (InputStream defaultInput = getClass().getClassLoader()
                        .getResourceAsStream("config.properties")) {
                    if (defaultInput != null) {
                        properties.load(defaultInput);
                    }
                }
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + configFile, e);
        }
    }

    public String getBaseUrl() {
        return System.getProperty("base.url", properties.getProperty("base.url"));
    }

    public String getAdminUsername() {
        return System.getProperty("admin.username", properties.getProperty("admin.username"));
    }

    public String getAdminPassword() {
        return System.getProperty("admin.password", properties.getProperty("admin.password"));
    }

    public int getRequestTimeout() {
        String timeout = System.getProperty("request.timeout", properties.getProperty("request.timeout", "30000"));
        return Integer.parseInt(timeout);
    }
}
