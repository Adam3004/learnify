package com.brightpath.learnify.config;

import com.brightpath.learnify.domain.common.RandomUuidProvider;
import com.brightpath.learnify.domain.common.UuidProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LearnifyConfiguration {
    @Bean
    public UuidProvider uuidProvider() {
        return new RandomUuidProvider();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }
}
