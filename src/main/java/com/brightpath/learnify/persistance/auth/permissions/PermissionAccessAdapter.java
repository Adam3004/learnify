package com.brightpath.learnify.persistance.auth.permissions;

import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.PermissionsAccess;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.badrequest.UserAccessIsAlreadyGrantedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionAccessAdapter {
    private final PermissionsAccessRepository permissionAccessRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public PermissionsAccess getFullPermissionsForResource(UUID resourceId, ResourceType convertedResourceType) {
        return permissionAccessRepository.findById(permissionAccessId(resourceId, convertedResourceType))
                .map(persistentMapper::asPermissionsAccess)
                .orElseThrow(() -> new ResourceNotFoundException(convertedResourceType));
    }

    // permission access id is based on the resource id and type
    private String permissionAccessId(UUID resourceId, ResourceType resourceType) {
        return resourceType.toString() + ":" + resourceId.toString();
    }

    public String getOwnerIdOfResource(UUID resourceId, ResourceType resourceType){
        PermissionsAccessEntity permissionsAccessModel = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));
        return permissionsAccessModel.getOwnerId();
    }

    public List<Permission> getPermissionsForResource(UUID resourceId, ResourceType resourceType) {
        return permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType))
                .getPermissions()
                .stream().map(persistentMapper::asPermission)
                .toList();
    }

    public PermissionsAccessEntity savePermissionAccess(UUID resourceId, ResourceType resourceType, String ownerId, PermissionLevel permissionLevel) {
        PermissionsAccessEntity permissionsAccessEntity = new PermissionsAccessEntity();
        permissionsAccessEntity.setId(permissionAccessId(resourceId, resourceType));
        permissionsAccessEntity.setPermissionLevel(permissionLevel);
        permissionsAccessEntity.setResourceType(resourceType);
        permissionsAccessEntity.setResourceId(resourceId);
        permissionsAccessEntity.setOwnerId(ownerId);
        return permissionAccessRepository.save(permissionsAccessEntity);
    }

    public Permission addPermissionToResourceForUser(UUID resourceId, String userId, ResourceAccessEnum requestedAccess) {
        PermissionsAccessEntity resourcePermissionsEntity = permissionAccessRepository.findFirstByResourceId(resourceId);
        Set<PermissionEntity> permissions = resourcePermissionsEntity.getPermissions();
        if (permissions.stream().anyMatch(permission -> permission.getUserId().equals(userId))) {
            throw new UserAccessIsAlreadyGrantedException();
        }
        PermissionEntity permissionEntity = new PermissionEntity(uuidProvider.generateUuid(), userId, requestedAccess);
        permissions.add(permissionEntity);
        resourcePermissionsEntity.setPermissions(permissions);
        permissionAccessRepository.save(resourcePermissionsEntity);
        return new Permission(permissionEntity.getUserId(), permissionEntity.getAccess());
    }

    public Permission editPermissionToResourceForUser(UUID resourceId, String userId, ResourceAccessEnum requestedAccess) {
        PermissionsAccessEntity resourcePermissionsEntity = permissionAccessRepository.findFirstByResourceId(resourceId);
        Set<PermissionEntity> permissions = resourcePermissionsEntity.getPermissions();
        if (permissions.stream().anyMatch(permission -> permission.getUserId().equals(userId) && permission.getAccess().equals(requestedAccess))) {
            throw new UserAccessIsAlreadyGrantedException();
        }
        PermissionEntity permissionEntity = permissions.stream()
                .filter(permission -> permission.getUserId().equals(userId))
                .findFirst()
                .orElse(new PermissionEntity(uuidProvider.generateUuid(), userId, requestedAccess));
        permissions.remove(permissionEntity);
        permissionEntity.setAccess(requestedAccess);
        permissions.add(permissionEntity);
        resourcePermissionsEntity.setPermissions(permissions);
        permissionAccessRepository.save(resourcePermissionsEntity);
        return new Permission(permissionEntity.getUserId(), permissionEntity.getAccess());
    }

    public void deletePermissionToResourceForUser(UUID resourceId, String userId) {
        PermissionsAccessEntity resourcePermissionsEntity = permissionAccessRepository.findFirstByResourceId(resourceId);
        Set<PermissionEntity> permissions = resourcePermissionsEntity.getPermissions();
        Optional<PermissionEntity> permissionEntity = permissions.stream()
                .filter(permission -> permission.getUserId().equals(userId))
                .findFirst();
        if (permissionEntity.isPresent()) {
            permissions.remove(permissionEntity.get());
            resourcePermissionsEntity.setPermissions(permissions);
            permissionAccessRepository.save(resourcePermissionsEntity);
        }
    }

    public void deletePermissionToResource(UUID resourceId) {
        permissionAccessRepository.deleteAllByResourceId(resourceId);
    }

    @Transactional
    public int editResourcePermissionModel(UUID resourceId, ResourceType resourceType, PermissionLevel permissionLevel) {
        return permissionAccessRepository.editResourcePermissionModel(permissionAccessId(resourceId, resourceType), permissionLevel);
    }
}
