package com.brightpath.learnify.domain.workspace;

import java.util.UUID;

public record Workspace(
        UUID id,
        String displayName
){}
