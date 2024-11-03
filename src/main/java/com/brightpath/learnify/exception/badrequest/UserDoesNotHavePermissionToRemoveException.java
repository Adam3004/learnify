package com.brightpath.learnify.exception.badrequest;

public class UserDoesNotHavePermissionToRemoveException extends RuntimeException {
    private static final String MESSAGE = "User is not permitted to remove the requested resource";

    public UserDoesNotHavePermissionToRemoveException() {
        super(MESSAGE);
    }
}
