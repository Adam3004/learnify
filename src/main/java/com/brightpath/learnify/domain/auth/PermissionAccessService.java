package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessSummary;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.auth.permissions.PermissionEntity;
import com.brightpath.learnify.persistance.auth.permissions.PermissionRepository;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessRepository;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResourceAccessEnum getUserAccessForResource(String userId, UUID resourceId, ResourceType resourceType) {
        ResourceAccessSummary access = permissionAccessRepository.findUserAccessToResource(userId, permissionAccessId(resourceId, resourceType));
        if (access.ownerId().equals(userId)) {
            return ResourceAccessEnum.OWNER;
        }
        return switch (access.permissionLevel()) {
            case PUBLIC -> {
                if (access.resourceAccessEnum() == null) {
                    yield ResourceAccessEnum.READ_WRITE;
                }else {
                    yield ResourceAccessEnum.DENIED;
                }
            }
            case PRIVATE -> {
                if (access.resourceAccessEnum() != null) {
                    yield access.resourceAccessEnum();
                }else {
                    yield ResourceAccessEnum.DENIED;
                }
            }
        };
    }

    // method for checking if user has access to a resource with a given id and type and requested access level (or lower)
    public boolean hasUserAccessToResource(String userId, UUID resourceId, ResourceType resourceType, ResourceAccessEnum requestedResourceAccessEnum) {
        ResourceAccessEnum userResourceAccessEnum = getUserAccessForResource(userId, resourceId, resourceType);
        return userResourceAccessEnum.isGreaterOrEqual(requestedResourceAccessEnum);
    }

    // method for returning all permissions for a resource with a given id and type
    public List<Permission> getPermissionsForResource(UUID resourceId, ResourceType resourceType) {
        return permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType))
                .getPermissions()
                .stream().map(persistentMapper::asPermission).toList();
    }
    // method for saving a default permission access for a resource with a given id and type
    public void saveDefaultPermissionAccess(UUID resourceId, ResourceType resourceType, String ownerId) {
        PermissionsAccessEntity permissionsAccessEntity = new PermissionsAccessEntity();
        permissionsAccessEntity.setId(permissionAccessId(resourceId, resourceType));
        permissionsAccessEntity.setPermissionLevel(PermissionLevel.PUBLIC);
        permissionsAccessEntity.setResourceType(resourceType);
        permissionsAccessEntity.setResourceId(resourceId);
        permissionsAccessEntity.setOwnerId(ownerId);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // method for edition a permission access for a resource with a given id and type (adding user with access)
    @Transactional
    public void addUserWithAccessToResource(UUID resourceId, ResourceType resourceType, String userId, ResourceAccessEnum resourceAccessEnum) {
        if(resourceAccessEnum == ResourceAccessEnum.OWNER) {
            throw new IllegalArgumentException("Cannot add user with OWNER access");
        }

        PermissionsAccessEntity permissionsAccessEntity = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setId(uuidProvider.generateUuid());
        permissionEntity.setUserId(userId);
        permissionEntity.setAccess(resourceAccessEnum);
        PermissionEntity permission = permissionRepository.save(permissionEntity);
        permissionsAccessEntity.addPermission(permission);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // method for removing a permission access for a resource with a given id and type (removing user with access)
    public void removeUserWithAccessToResource(UUID resourceId, ResourceType resourceType, String userId) {
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));

        if(permissionsAccessEntity.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Cannot remove owner access");
        }

        permissionsAccessEntity.removePermissionForUser(userId);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // permission access id is based on the resource id and type
    private String permissionAccessId(UUID resourceId, ResourceType resourceType) {
        return resourceType.toString() + ":" + resourceId.toString();
    }

}
