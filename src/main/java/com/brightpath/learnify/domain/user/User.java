package com.brightpath.learnify.domain.user;

public record User(
        String id,
        String displayName,
        String email
) {}
