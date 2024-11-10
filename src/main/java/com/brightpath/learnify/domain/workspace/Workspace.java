package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.domain.user.User;

import java.util.UUID;

public record Workspace(
        UUID id,
        String displayName,
        User owner
){}
