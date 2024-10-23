package com.brightpath.learnify.config;


import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class FirebaseAuthentication implements org.springframework.security.core.Authentication {

    private final FirebaseToken decodedToken;
    private Object authenticationDetails;
    private boolean authenticated = true;

    public FirebaseAuthentication(FirebaseToken decodedToken) {
        this.decodedToken = decodedToken;
    }

    public void setDetails(Object webAuthenticationDetails) {
        this.authenticationDetails = webAuthenticationDetails;
    }

    @Override
    public Object getDetails() {
        return authenticationDetails;
    }

    @Override
    public Object getPrincipal() {
        return decodedToken.getUid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public Object getCredentials() {
        return decodedToken;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }


    @Override
    public String getName() {
        return "Firebase user";
    }
}
