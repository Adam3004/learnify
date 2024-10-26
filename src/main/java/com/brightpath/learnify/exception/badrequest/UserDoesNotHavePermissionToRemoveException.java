package com.brightpath.learnify.exception.badrequest;

public class UserDoesNotHavePermissionToRemoveException extends RuntimeException {
    private static final String MESSAGE = "User do not have permission which was requested to be removed";

    public UserDoesNotHavePermissionToRemoveException() {
        super(MESSAGE);
    }
}
