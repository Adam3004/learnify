package com.brightpath.learnify.exception.badrequest;

public class FinalValueEditionRequestedException extends RuntimeException {
    private static final String MESSAGE = "Unmodifiable value was requested to change";

    public FinalValueEditionRequestedException() {
        super(MESSAGE);
    }
}
