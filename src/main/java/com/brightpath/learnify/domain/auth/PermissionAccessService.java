package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.auth.permission.FullResourcePermissionModel;
import com.brightpath.learnify.domain.auth.permission.Permission;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.brightpath.learnify.domain.auth.permission.PermissionLevel.PRIVATE;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.DENIED;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.OWNER;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_WRITE;

@Service
@RequiredArgsConstructor
public class PermissionAccessService {
    private final PermissionsAccessRepository permissionAccessRepository;
    private final PermissionRepository permissionRepository;
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
        ResourceAccessSummary access = permissionAccessRepository.findUserAccessToResource(userId, permissionAccessId(resourceId, resourceType));
        if (access == null) {
            throw new ResourceNotFoundException(resourceType);
        }
        if (access.ownerId().equals(userId)) {
            return OWNER;
        }
        return switch (access.permissionLevel()) {
            case PUBLIC -> {
                if (access.resourceAccessEnum() == null) {
                    yield READ_WRITE;
                } else {
                    yield access.resourceAccessEnum();
                }
            }
            case PRIVATE -> {
                if (access.resourceAccessEnum() != null) {
                    yield access.resourceAccessEnum();
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

    // method for saving a default permission access for a resource with a given id and type
    public void saveDefaultPermissionAccess(UUID resourceId, ResourceType resourceType, String ownerId) {
        PermissionsAccessEntity permissionsAccessEntity = new PermissionsAccessEntity();
        permissionsAccessEntity.setId(permissionAccessId(resourceId, resourceType));
        permissionsAccessEntity.setPermissionLevel(PRIVATE);
        permissionsAccessEntity.setResourceType(resourceType);
        permissionsAccessEntity.setResourceId(resourceId);
        permissionsAccessEntity.setOwnerId(ownerId);
        permissionAccessRepository.save(permissionsAccessEntity);
    }

    // method for edition a permission access for a resource with a given id and type (adding user with access)
    @Transactional
    public void addUserWithAccessToResource(UUID resourceId, ResourceType resourceType, String userId, ResourceAccessEnum resourceAccessEnum) {
        if (resourceAccessEnum == OWNER) {
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

        if (permissionsAccessEntity.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Cannot remove owner access");
        }

        permissionsAccessEntity.removePermissionForUser(userId);
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
        if (userHasAnyPermissionToResource(resourceId, resourceType, userId)) {
            throw new UserAccessIsAlreadyGrantedException();
        }
        PermissionsAccessEntity resourcePermissionsEntity = permissionAccessRepository.findFirstByResourceId(resourceId);
        PermissionEntity permissionEntity = new PermissionEntity(uuidProvider.generateUuid(), userId, requestedAccess);
        Set<PermissionEntity> permissions = resourcePermissionsEntity.getPermissions();
        permissions.add(permissionEntity);
        resourcePermissionsEntity.setPermissions(permissions);
        permissionAccessRepository.save(resourcePermissionsEntity);
        return new Permission(permissionEntity.getUserId(), permissionEntity.getAccess());
    }

    public Permission editPermissionToResourceForUser(UUID resourceId, ResourceType resourceType, String userId, ResourceAccessEnum requestedAccess) {
        userService.checkIfUserExists(userId);
        if (userHasExactPermissionToResource(resourceId, resourceType, userId, Optional.of(requestedAccess.getOppositeStatus()))) {
            throw new UserAccessIsAlreadyGrantedException();
        }
        PermissionsAccessEntity resourcePermissionsEntity = permissionAccessRepository.findFirstByResourceId(resourceId);
        Set<PermissionEntity> permissions = resourcePermissionsEntity.getPermissions();
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

    private boolean userHasAnyPermissionToResource(UUID resourceId, ResourceType resourceType, String userId) {
        return checkUsersPermission(resourceId, resourceType, userId, Optional.empty());
    }

    private boolean userHasExactPermissionToResource(UUID resourceId, ResourceType resourceType, String userId, Optional<ResourceAccessEnum> access) {
        return checkUsersPermission(resourceId, resourceType, userId, access);
    }

    private boolean checkUsersPermission(UUID resourceId, ResourceType resourceType, String userId, Optional<ResourceAccessEnum> access) {
        if (access.isPresent()) {
            if (access.get().equals(READ_ONLY)) {
                return hasUserAccessToResource(userId, resourceId, resourceType, READ_ONLY);
            } else {
                return hasUserAccessToResource(userId, resourceId, resourceType, READ_WRITE);
            }
        }
        return hasUserAccessToResource(userId, resourceId, resourceType, READ_ONLY) ||
                hasUserAccessToResource(userId, resourceId, resourceType, READ_WRITE);
    }

    // permission access id is based on the resource id and type
    private String permissionAccessId(UUID resourceId, ResourceType resourceType) {
        return resourceType.toString() + ":" + resourceId.toString();
    }
}
