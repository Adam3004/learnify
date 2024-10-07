package com.brightpath.learnify.exception.notfound;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(ResourceType resourceType) {
        super(resourceType.getReadableValue() + " was not found");
    }
}
