package com.brightpath.learnify.exception.mapper;

import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToEditException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToGetException;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ExceptionMapper {
    private static final String BAD_REQUEST_DEFAULT_MESSAGE = "Bad request, reason: ";

    //unauthorized
    @ExceptionHandler
    public ResponseEntity<?> onAuthenticationToGetFailedException(UserNotAuthorizedToGetException ex) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> onAuthenticationToEditFailedException(UserNotAuthorizedToEditException ex) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(ex.getMessage());
    }

    //not found
    @ExceptionHandler
    public ResponseEntity<?> onLookingForResourceFailedException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(ex.getMessage());
    }

    //bad request
    @ExceptionHandler
    public ResponseEntity<?> onUpdatingQuizResultFailedException(UpdatingQuizResultsFailedException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ex.getMessage());
    }

    //default
    @ExceptionHandler
    public ResponseEntity<?> onOtherActionsFailedException(RuntimeException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(BAD_REQUEST_DEFAULT_MESSAGE + ex.getMessage());
    }
}
