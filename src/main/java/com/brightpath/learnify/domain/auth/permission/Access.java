package com.brightpath.learnify.domain.auth.permission;

public enum Access {
    DENIED,
    READ_ONLY,
    READ_WRITE;

    public boolean isGreaterOrEqual(Access access) {
        return switch (access) {
            case DENIED -> false;
            case READ_ONLY -> this != DENIED;
            case READ_WRITE -> this == READ_WRITE;
        };
    }
}
