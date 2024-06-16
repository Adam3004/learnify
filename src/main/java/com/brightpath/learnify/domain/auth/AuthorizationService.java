package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.persistance.common.User;

public interface AuthorizationService {
    User authorize(String token);
    User defaultUser();
}
