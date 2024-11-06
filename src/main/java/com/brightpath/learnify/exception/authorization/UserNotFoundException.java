package com.brightpath.learnify.exception.authorization;

public class UserNotFoundException extends RuntimeException {
    private static final String MESSAGE = "User with id %s was not found";

    public UserNotFoundException(String userId) {
        super(String.format(MESSAGE, userId));
    }
}
