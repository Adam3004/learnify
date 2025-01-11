package com.brightpath.learnify.domain.auth.port;

import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.PermissionsAccess;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PermissionAccessPersistencePort {
    PermissionsAccess getFullPermissionsForResource(UUID resourceId, ResourceType convertedResourceType);

    String getOwnerIdOfResource(UUID resourceId, ResourceType resourceType);

    List<Permission> getPermissionsForResource(UUID resourceId, ResourceType resourceType);

    PermissionsAccessEntity savePermissionAccess(UUID resourceId, ResourceType resourceType, String ownerId, PermissionLevel permissionLevel);

    Permission addPermissionToResourceForUser(UUID resourceId, String userId, ResourceAccessEnum requestedAccess);

    Permission editPermissionToResourceForUser(UUID resourceId, String userId, ResourceAccessEnum requestedAccess);

    void deletePermissionToResourceForUser(UUID resourceId, String userId);

    void deletePermissionToResource(UUID resourceId);

    @Transactional
    int editResourcePermissionModel(UUID resourceId, ResourceType resourceType, PermissionLevel permissionLevel);
}
