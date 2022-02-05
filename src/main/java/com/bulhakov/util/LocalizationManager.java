package com.bulhakov.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class LocalizationManager {

    private Map<String, ResourceBundle> availableResources;

    private ResourceBundle currentResource;

    @Autowired
    public void setAvailableLocales(Map<String, ResourceBundle> resources){
        this.availableResources = resources;
        currentResource = resources.values().stream().findFirst().orElse(null);
    }

    public void setResource(String resourceId){
        ResourceBundle resourceFound = availableResources.get(resourceId);
        if(resourceFound == null){
            throw new IllegalArgumentException("No such resource found");
        }
        currentResource = resourceFound;
    }

    public String getStringFromResource(String key){
        String translate = currentResource.getString(key);
        return new String(translate.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}