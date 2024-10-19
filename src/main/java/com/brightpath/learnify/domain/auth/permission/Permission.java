package com.brightpath.learnify.domain.auth.permission;

public record Permission(
        String userId,
        Access access
) {
}
