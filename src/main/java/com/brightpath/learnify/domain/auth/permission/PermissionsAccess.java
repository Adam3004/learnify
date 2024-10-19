package com.brightpath.learnify.domain.auth.permission;

import com.brightpath.learnify.exception.notfound.ResourceType;

import java.util.List;
import java.util.UUID;

public record PermissionsAccess(
        PermissionLevel permissionLevel,
        List<Permission> permissions,
        ResourceType resourceType,
        UUID resourceId
) {
}
