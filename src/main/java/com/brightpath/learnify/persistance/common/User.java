package com.brightpath.learnify.persistance.common;

import java.util.UUID;

public record User(
        UUID uuid,
        String displayName,
        String email
) {}
