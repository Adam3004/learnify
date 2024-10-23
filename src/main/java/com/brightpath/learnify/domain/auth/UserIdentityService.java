package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserIdentityService {

    private final UserService userService;

    public User getCurrentUser() {
        // TODO use the userService to get the user by current uid
        throw new NotImplementedException("Not implemented");
    }

    public boolean isUserAuthenticated() {
        return getCurrentAuthentication().isAuthenticated();
    }

    public String getCurrentUserId() {
        return getCurrentUserIdFromPrincipal();
    }

    public boolean isCurrentUserAdmin() {
        // TODO use the current user authorities to check if the current user is an admin
        throw new NotImplementedException("Not implemented");
    }

    private String getCurrentUserIdFromPrincipal() {
        return (String) getCurrentAuthentication().getPrincipal();
    }

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
