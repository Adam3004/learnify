package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.user.User;

public interface AuthorizationService {
    User authorize(String token);
    User defaultUser();
}
