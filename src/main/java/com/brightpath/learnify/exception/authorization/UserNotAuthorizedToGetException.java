package com.brightpath.learnify.exception.authorization;

public class UserNotAuthorizedToGetException extends RuntimeException{
    private static final String MESSAGE = "User is not permitted to get the requested resource";

    public UserNotAuthorizedToGetException() {
        super(MESSAGE);
    }
}
