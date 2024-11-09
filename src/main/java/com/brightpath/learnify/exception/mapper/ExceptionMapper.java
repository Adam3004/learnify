package com.brightpath.learnify.exception.mapper;

import com.brightpath.learnify.exception.authorization.UserIsNotResourceOwnerException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToEditException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToGetException;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.badrequest.UserAccessIsAlreadyGrantedException;
import com.brightpath.learnify.exception.badrequest.UserDoesNotHavePermissionToRemoveException;
import com.brightpath.learnify.exception.conflict.ResourceUpdateConflictException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ExceptionMapper {
    private static final String BAD_REQUEST_DEFAULT_MESSAGE = "Bad request, reason: ";

    @ExceptionHandler(ResourceUpdateConflictException.class)
    public ResponseEntity<String> handleResourceUpdateConflictException(ResourceUpdateConflictException ex) {
        return ResponseEntity
                .status(CONFLICT)
                .body(ex.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body("Access denied: " + ex.getMessage());
    }

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

    @ExceptionHandler
    public ResponseEntity<?> onAuthenticationToOweFailedException(UserIsNotResourceOwnerException ex) {
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

    @ExceptionHandler
    public ResponseEntity<?> onLookingForEntityFailedException(EntityNotFoundException ex) {
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

    @ExceptionHandler
    public ResponseEntity<?> onUserDoesNotHavePermissionToRemoveException(UserDoesNotHavePermissionToRemoveException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> onUserAccessIsAlreadyGrantedException(UserAccessIsAlreadyGrantedException ex) {
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
