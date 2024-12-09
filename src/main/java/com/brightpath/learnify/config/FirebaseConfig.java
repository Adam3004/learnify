package com.brightpath.learnify.config;

import com.brightpath.learnify.config.properties.FirebaseProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;

    public FirebaseConfig(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Create a map to hold the service account details
        Map<String, Object> serviceAccount = new HashMap<>();
        serviceAccount.put("type", "service_account");
        serviceAccount.put("project_id", firebaseProperties.getProjectId());
        serviceAccount.put("private_key_id", firebaseProperties.getPrivateKeyId());
        serviceAccount.put("private_key", firebaseProperties.getPrivateKey().replace("\\n", "\n")); // Make sure to replace escaped new lines
        serviceAccount.put("client_email", firebaseProperties.getClientEmail());
        serviceAccount.put("client_id", firebaseProperties.getClientId());
        serviceAccount.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
        serviceAccount.put("token_uri", "https://oauth2.googleapis.com/token");
        serviceAccount.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
        serviceAccount.put("client_x509_cert_url", firebaseProperties.getClientX509CertUrl());

        // Build Firebase options
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(convertMapToJson(serviceAccount).getBytes())))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    private String convertMapToJson(Map<String, Object> map) throws JsonProcessingException {
        // Use your preferred method to convert the map to JSON format
        // You could use a library like Jackson or Gson for this
        return new ObjectMapper().writeValueAsString(map);
    }
}
