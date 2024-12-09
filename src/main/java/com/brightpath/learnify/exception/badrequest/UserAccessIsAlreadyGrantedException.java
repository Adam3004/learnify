package com.brightpath.learnify.exception.badrequest;

public class UserAccessIsAlreadyGrantedException extends RuntimeException {
    private static final String MESSAGE = "User has already granted this permission";

    public UserAccessIsAlreadyGrantedException() {
        super(MESSAGE);
    }
}
