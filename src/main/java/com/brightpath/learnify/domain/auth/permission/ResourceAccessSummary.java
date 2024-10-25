package com.brightpath.learnify.domain.auth.permission;

import com.brightpath.learnify.domain.common.ResourceType;

import java.util.UUID;

public record ResourceAccessSummary(
        PermissionLevel permissionLevel,
        UUID resourceId,
        ResourceType resourceType,
        ResourceAccessEnum resourceAccessEnum,
        String ownerId
) {
}
