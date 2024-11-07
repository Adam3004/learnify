package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.auth.permission.FullResourcePermissionModel;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.PermissionsAccess;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessSummary;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.exception.badrequest.UserAccessIsAlreadyGrantedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.auth.permissions.PermissionEntity;
import com.brightpath.learnify.persistance.auth.permissions.PermissionRepository;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessRepository;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.DENIED;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.OWNER;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_WRITE;

@Service
@RequiredArgsConstructor
public class PermissionAccessService {
    private final PermissionsAccessRepository permissionAccessRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final UserIdentityService userIdentityService;
    private final UserService userService;

    public FullResourcePermissionModel getFullPermissionsForResource(UUID resourceId, ResourceType convertedResourceType) {
        PermissionsAccess access = permissionAccessRepository.findById(permissionAccessId(resourceId, convertedResourceType))
                .map(persistentMapper::asPermissionsAccess)
                .orElseThrow(() -> new ResourceNotFoundException(convertedResourceType));
        List<User> users = userService.getUsersByIds(access.permissions().stream().map(Permission::userId).toList());
        Map<User, ResourceAccessEnum> userPermissions = access.permissions().stream()
                .collect(Collectors.toMap(permission -> users.stream().filter(user -> user.id().equals(permission.userId())).findFirst().get(), Permission::resourceAccessEnum));
        return new FullResourcePermissionModel(
                access.resourceType(),
                access.resourceId(),
                access.permissionLevel(),
                userPermissions
        );
    }


    // method for checking if user has access to a resource with a given id and type
    public ResourceAccessEnum getUserAccessForResource(String userId, UUID resourceId, ResourceType resourceType) {
        PermissionsAccessEntity permissionsAccessModel = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));
        if (permissionsAccessModel.getOwnerId().equals(userId)) {
            return OWNER;
        }
        ResourceAccessEnum userAccess = permissionsAccessModel.getPermissions().stream()
                .filter(permission -> permission.getUserId().equals(userId))
                .findFirst()
                .map(PermissionEntity::getAccess)
                .orElse(null);
        return switch (permissionsAccessModel.getPermissionLevel()) {
            case PUBLIC -> {
                if (userAccess == null) {
                    yield READ_WRITE;
                } else {
                    yield userAccess;
                }
            }
            case PRIVATE -> {
                if (userAccess != null) {
                    yield userAccess;
                } else {
                    yield DENIED;
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
                .stream().map(persistentMapper::asPermission)
                .toList();
    }

    // method for saving a permission access for a resource with a given id and type
    public void savePermissionAccess(UUID resourceId, ResourceType resourceType, String ownerId, PermissionLevel permissionLevel) {
        PermissionsAccessEntity permissionsAccessEntity = new PermissionsAccessEntity();
        permissionsAccessEntity.setId(permissionAccessId(resourceId, resourceType));
        permissionsAccessEntity.setPermissionLevel(permissionLevel);
        permissionsAccessEntity.setResourceType(resourceType);
        permissionsAccessEntity.setResourceId(resourceId);
        permissionsAccessEntity.setOwnerId(ownerId);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    /**
     * Method used in PreAuthorize annotations to check if the current user has permission to edit a requested resource
     */
    @SuppressWarnings("unused")
    public boolean checkUserPermissionToEditResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        return hasUserAccessToResource(userId, resourceId, resourceType, READ_WRITE);
    }

    /**
     * Method used in PreAuthorize annotations to check if the current user has permission to view a requested resource
     */
    @SuppressWarnings("unused")
    public boolean checkUserPermissionToViewResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        return hasUserAccessToResource(userId, resourceId, resourceType, READ_ONLY);
    }

    /**
     * Method used in PreAuthorize annotations to check if the current user is the owner of a requested resource
     */
    @SuppressWarnings("unused")
    public boolean checkIfUserIsOwnerOfResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessRepository.findById(permissionAccessId(resourceId, resourceType))
                .orElseThrow(() -> new ResourceNotFoundException(resourceType));
        return permissionsAccessEntity.getOwnerId().equals(userId);
    }

    public Permission addPermissionToResourceForUser(UUID resourceId, ResourceType resourceType, String userId, ResourceAccessEnum requestedAccess) {
        userService.checkIfUserExists(userId);
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

    public Permission editPermissionToResourceForUser(UUID resourceId, ResourceType resourceType, String userId, ResourceAccessEnum requestedAccess) {
        userService.checkIfUserExists(userId);
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
        userService.checkIfUserExists(userId);
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

    // permission access id is based on the resource id and type
    private String permissionAccessId(UUID resourceId, ResourceType resourceType) {
        return resourceType.toString() + ":" + resourceId.toString();
    }
}
