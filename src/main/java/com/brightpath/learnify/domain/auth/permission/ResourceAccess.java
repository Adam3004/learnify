package com.brightpath.learnify.domain.auth.permission;

import com.brightpath.learnify.domain.common.ResourceType;

import java.util.UUID;

public record ResourceAccess(
        UUID resourceId,
        ResourceType resourceType,
        Access access
) {
}
