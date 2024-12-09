package com.brightpath.learnify.domain.auth.permission;

public record Permission(
        String userId,
        ResourceAccessEnum resourceAccessEnum
) {
}
