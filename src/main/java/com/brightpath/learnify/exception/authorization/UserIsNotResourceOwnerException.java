package com.brightpath.learnify.exception.authorization;

public class UserIsNotResourceOwnerException extends RuntimeException{
    private static final String MESSAGE = "User is not the owner of the requested resource";

    public UserIsNotResourceOwnerException() {
        super(MESSAGE);
    }
}
