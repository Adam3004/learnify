package com.brightpath.learnify;

import com.brightpath.learnify.config.properties.FirebaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FirebaseProperties.class)
public class LearnifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnifyApplication.class, args);
    }

}
