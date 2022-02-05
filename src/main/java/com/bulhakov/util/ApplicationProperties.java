package com.bulhakov.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Component
public class ApplicationProperties {

    private static final String SETTINGS_FILEPATH = "src/main/resources/settings.properties";

    private Properties settings;

    @Autowired
    private ApplicationProperties() throws IOException {
        settings = new Properties();
        settings.load(new FileReader(SETTINGS_FILEPATH));
    }

    public String getProperty(String key){
        return settings.getProperty(key);
    }
}
