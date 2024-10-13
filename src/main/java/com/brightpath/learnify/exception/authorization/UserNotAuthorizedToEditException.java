package com.brightpath.learnify.exception.authorization;

public class UserNotAuthorizedToEditException extends RuntimeException{
    private static final String MESSAGE = "User is not permitted to edit the requested resource";

    public UserNotAuthorizedToEditException() {
        super(MESSAGE);
    }
}
