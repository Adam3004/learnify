package com.brightpath.learnify.persistance.auth.permissions;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.common.ResourceType;

import java.util.UUID;

public record SimplePermissionsAccessProjection(
        UUID id,
        PermissionLevel permissionLevel,
        ResourceType resourceType,
        UUID resourceId) {
}
