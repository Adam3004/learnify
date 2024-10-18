package com.brightpath.learnify.domain.user;

import java.util.UUID;

public record User(
        UUID id,
        String displayName,
        String email
) {}
