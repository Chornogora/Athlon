package com.bulhakov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Solution to display logs on Railway
        System.setErr(System.out);
        SpringApplication.run(Application.class, args);
    }
}