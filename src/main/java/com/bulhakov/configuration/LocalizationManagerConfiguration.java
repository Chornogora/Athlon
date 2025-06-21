package com.bulhakov.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Configuration
public class LocalizationManagerConfiguration {

    @Bean
    public Map<String, ResourceBundle> availableResources(){
        Map<String, ResourceBundle> resources = new HashMap<>();
        ResourceBundle ruBundle = ResourceBundle.getBundle("messages", new Locale("RU", "ua"));
        resources.put("Ru", ruBundle);
        ResourceBundle enBundle = ResourceBundle.getBundle("messages", new Locale("EN", "us"));
        resources.put("En", enBundle);
        return resources;
    }
}