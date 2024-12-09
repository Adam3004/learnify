package com.brightpath.learnify.controller;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/internal")
@Profile("dev") // Make this controller active only in the 'dev' profile
public class AdminController {

    private final FirebaseAuth firebaseAuth;

    public AdminController(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @PreAuthorize("""
            @userIdentityService.isCurrentUserAdmin()
    """)
    @PostMapping("/set-sysadmin/{uid}")
    public ResponseEntity<String> setSysadminClaim(@PathVariable String uid) {
        try {
            firebaseAuth.setCustomUserClaims(uid, Collections.singletonMap("sysadmin", true));

            return ResponseEntity.ok("Custom claim 'sysadmin' set for user: " + uid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error setting custom claims: " + e.getMessage());
        }
    }
}

