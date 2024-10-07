package com.brightpath.learnify.exception.badrequest;

public class UpdatingQuizResultsFailedException extends RuntimeException {
    private static final String MESSAGE = "Something went wrong during updating quiz results";

    public UpdatingQuizResultsFailedException() {
        super(MESSAGE);
    }
}
