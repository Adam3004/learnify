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

    public ResourceAccessEnum getOppositeStatus() {
        return switch (this) {
            case READ_WRITE -> READ_ONLY;
            case READ_ONLY -> READ_WRITE;
            case OWNER -> DENIED;
            case DENIED -> OWNER;
        };
    }
}
