package com.example.personmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class PersonManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonManagementApplication.class, args);
    }

}
