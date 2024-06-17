package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class FakeAuthorizationService implements AuthorizationService {

    private final User user;
    private final UserService userService;

    public FakeAuthorizationService(UserService userService) {
        this.userService = userService;
        user = userService.createUser("usnarski@gmail.com", "Krzysztof Usnarski");
    }

    @Override
    public User authorize(String token) {
        return user;
    }

    @Override
    public User defaultUser() {
        return user;
    }
}
