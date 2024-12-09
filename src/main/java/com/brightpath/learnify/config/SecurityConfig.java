package com.brightpath.learnify.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // Allow public access to Swagger UI and API docs
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                // All other requests under /api require authentication
                                .requestMatchers( "/api/**").authenticated()
                )
                .addFilterAfter(new FirebaseAuthenticationFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling(exp -> exp.authenticationEntryPoint(new
                        HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }


}
