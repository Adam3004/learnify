package com.brightpath.learnify.domain.auth.permission;

import java.util.UUID;

public record Permission(
        UUID userId,
        Access access
) {
}
