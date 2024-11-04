package com.brightpath.learnify.persistance.user;

public record UserQueryFilter(
        String email,
        String displayName
) {}
