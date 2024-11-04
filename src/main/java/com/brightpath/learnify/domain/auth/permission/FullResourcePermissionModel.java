package com.brightpath.learnify.domain.auth.permission;

import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.user.User;

import java.util.Map;
import java.util.UUID;

public record FullResourcePermissionModel(
        ResourceType resourceType,
        UUID resourceId,
        PermissionLevel permissionLevel,
        Map<User, ResourceAccessEnum> userPermissions
) {
}
