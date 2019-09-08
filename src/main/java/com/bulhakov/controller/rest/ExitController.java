package com.bulhakov.controller.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExitController implements ApplicationContextAware {

    private ApplicationContext context;

    @GetMapping("/exit")
    public void exit(){
        int exitCode = SpringApplication.exit(context, ()->0);
        System.exit(exitCode);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        this.context = applicationContext;
    }
}