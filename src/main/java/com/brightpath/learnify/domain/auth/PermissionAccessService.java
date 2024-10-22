package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.auth.permission.Access;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.ResourceAccess;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.auth.permissions.*;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionAccessService {

    private final PermissionsAccessRepository permissionAccessRepository;

    private final PermissionRepository permissionRepository;

    private final PersistentMapper persistentMapper;

    private final UuidProvider uuidProvider;


    // method for checking if user has access to a resource with a given id and type
    public Access getUserAccessForResource(UUID userId, UUID resourceId, ResourceType resourceType) {
        return permissionAccessRepository.findUserAccessToResource(userId, permissionAccessId(resourceId, resourceType))
                .map(ResourceAccessProjection::access)
                .orElse(Access.DENIED);
    }

    public boolean hasUserAccessToResource(UUID userId, UUID resourceId, ResourceType resourceType, Access access) {
        Access userAccess = getUserAccessForResource(userId, resourceId, resourceType);
        return userAccess.isGreaterOrEqual(access);
    }

    // method for returning all permissions for a resource with a given id and type
    public List<Permission> getPermissionsForResource(UUID resourceId, ResourceType resourceType) {
        return permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType))
                .getPermissions()
                .stream().map(persistentMapper::asPermission).toList();
    }

    // method for returning all resources with a given type to which the user has access (with a given access level) query ONLY THE 100 RESOURCES - 100 SHOULD BE STATIC - USE PageRequest.of(0, 100)
    public List<ResourceAccess> getPublicResourcesWithUserAccess(UUID userId) {
        return permissionAccessRepository.findAllPublicResourcesWithUserAccess(Pageable.ofSize(100), userId)
                .map(persistentMapper::asResourceAccess).toList();
    }

    // method for saving a default permission access for a resource with a given id and type
    public void saveDefaultPermissionAccess(UUID resourceId, ResourceType resourceType) {
        PermissionsAccessEntity permissionsAccessEntity = new PermissionsAccessEntity();
        permissionsAccessEntity.setId(permissionAccessId(resourceId, resourceType));
        permissionsAccessEntity.setPermissionLevel(PermissionLevel.PUBLIC);
        permissionsAccessEntity.setResourceType(resourceType);
        permissionsAccessEntity.setResourceId(resourceId);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // method for edition a permission access for a resource with a given id and type (adding user with access)
    public void addUserWithAccessToResource(UUID resourceId, ResourceType resourceType, String userId, Access access) {
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setId(uuidProvider.generateUuid());
        permissionEntity.setUserId(userId);
        permissionEntity.setAccess(access);
        PermissionEntity permission = permissionRepository.save(permissionEntity);
        permissionsAccessEntity.getPermissions().add(permission);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // method for removing a permission access for a resource with a given id and type (removing user with access)
    public void removeUserWithAccessToResource(UUID resourceId, ResourceType resourceType, String userId) {
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));

        permissionsAccessEntity.getPermissions().removeIf(p -> p.getUserId().equals(userId));
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // permission access id is based on the resource id and type
    private String permissionAccessId(UUID resourceId, ResourceType resourceType) {
        return resourceType.toString() + ":" + resourceId.toString();
    }

}
