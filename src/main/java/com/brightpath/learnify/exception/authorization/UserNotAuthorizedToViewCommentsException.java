package com.brightpath.learnify.exception.authorization;

public class UserNotAuthorizedToViewCommentsException extends RuntimeException {
    private static final String MESSAGE = "User is not authorized to add comment for this resource";

    public UserNotAuthorizedToViewCommentsException() {
        super(MESSAGE);
    }
}
