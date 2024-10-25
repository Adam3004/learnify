package com.brightpath.learnify.domain.auth.permission;

public enum ResourceAccessEnum {
    DENIED,
    READ_ONLY,
    READ_WRITE,
    OWNER;

    public boolean isGreaterOrEqual(ResourceAccessEnum resourceAccessEnum) {
        return switch (this) {
            case DENIED -> false;
            case READ_ONLY -> resourceAccessEnum == DENIED || resourceAccessEnum == READ_ONLY;
            case READ_WRITE -> resourceAccessEnum != OWNER;
            case OWNER -> true;
        };
    }
}
