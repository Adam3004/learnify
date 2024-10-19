package com.brightpath.learnify.exception.notfound;

import com.brightpath.learnify.domain.common.ResourceType;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(ResourceType resourceType) {
        super(resourceType.getReadableValue() + " was not found");
    }
}
