package com.brightpath.learnify.domain.auth.permission;

import java.util.List;

public record PermissionsAccess(
        PermissionLevel permissionLevel,
        List<Permission> permissions
) {
}
