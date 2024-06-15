package com.brightpath.learnify.persistance.common;

import java.util.UUID;

public record Workspace(
        UUID uuid,
        String displayName
){}
