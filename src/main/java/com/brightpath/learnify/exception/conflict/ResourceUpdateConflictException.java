package com.brightpath.learnify.exception.conflict;

import com.brightpath.learnify.domain.common.ResourceType;

import java.util.UUID;

public class ResourceUpdateConflictException extends RuntimeException {
    public ResourceUpdateConflictException(ResourceType type, UUID resourceId) {
        super(type.getReadableValue() + " with id " + resourceId + " cannot be updated due to version conflict");
    }
}
