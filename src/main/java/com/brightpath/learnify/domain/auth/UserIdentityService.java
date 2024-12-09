package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.config.FirebaseAuthentication;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserIdentityService {

    private final UserService userService;

    public User getCurrentUser() {
        return userService.getUserById(getCurrentUserIdFromPrincipal());
    }

    public boolean isUserAuthenticated() {
        return getCurrentAuthentication().isAuthenticated();
    }

    public String getCurrentUserId() {
        return getCurrentUserIdFromPrincipal();
    }

    /**
     * Method used in PreAuthorize annotations to check if the current user is an admin
     * */
    @SuppressWarnings("unused")
    public boolean isCurrentUserAdmin() {
        FirebaseToken decodedToken = ((FirebaseAuthentication) getCurrentAuthentication()).getDecodedToken();
        return (boolean) decodedToken.getClaims().getOrDefault("sysadmin", false);
    }

    private String getCurrentUserIdFromPrincipal() {
        return (String) getCurrentAuthentication().getPrincipal();
    }

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
