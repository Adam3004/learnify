package com.brightpath.learnify.domain.auth;

import com.brightpath.learnify.domain.auth.permission.FullResourcePermissionModel;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.PermissionsAccess;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.auth.permissions.PermissionAccessAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.DENIED;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.OWNER;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_WRITE;

@Service
@RequiredArgsConstructor
public class PermissionAccessService {
    private final UserIdentityService userIdentityService;
    private final UserService userService;
    private final PermissionAccessAdapter permissionAccessAdapter;

    public FullResourcePermissionModel getFullPermissionsForResource(UUID resourceId, ResourceType convertedResourceType) {
        PermissionsAccess access = permissionAccessAdapter.getFullPermissionsForResource(resourceId, convertedResourceType);
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
        FullResourcePermissionModel permissionsAccessModel = getFullPermissionsForResource(resourceId, resourceType);
        if (permissionAccessAdapter.getOwnerIdOfResource(resourceId, resourceType).equals(userId)) {
            return OWNER;
        }
        ResourceAccessEnum userAccess = permissionsAccessModel.userPermissions().entrySet().stream()
                .filter(permission -> permission.getKey().id().equals(userId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
        return switch (permissionsAccessModel.permissionLevel()) {
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
        return permissionAccessAdapter.getPermissionsForResource(resourceId, resourceType);
    }

    // method for saving a permission access for a resource with a given id and type
    public void savePermissionAccess(UUID resourceId, ResourceType resourceType, String ownerId, PermissionLevel permissionLevel) {
        permissionAccessAdapter.savePermissionAccess(resourceId, resourceType, ownerId, permissionLevel);
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
        String ownerId = permissionAccessAdapter.getOwnerIdOfResource(resourceId, resourceType);
        return ownerId.equals(userId);
    }

    public Permission addPermissionToResourceForUser(UUID resourceId, String userId, ResourceAccessEnum requestedAccess) {
        userService.checkIfUserExists(userId);
        return permissionAccessAdapter.addPermissionToResourceForUser(resourceId, userId, requestedAccess);
    }

    public Permission editPermissionToResourceForUser(UUID resourceId, String userId, ResourceAccessEnum requestedAccess) {
        userService.checkIfUserExists(userId);
        return permissionAccessAdapter.editPermissionToResourceForUser(resourceId, userId, requestedAccess);
    }

    public void deletePermissionToResourceForUser(UUID resourceId, String userId) {
        userService.checkIfUserExists(userId);
        permissionAccessAdapter.deletePermissionToResourceForUser(resourceId, userId);
    }

    public void deletePermissionToResource(UUID resourceId) {
        permissionAccessAdapter.deletePermissionToResource(resourceId);
    }


    @Transactional
    public PermissionLevel editResourcePermissionModel(UUID resourceId, ResourceType resourceType, PermissionLevel permissionLevel) {
        int affectedRows = permissionAccessAdapter.editResourcePermissionModel(resourceId, resourceType, permissionLevel);
        if (affectedRows == 0) {
            throw new ResourceNotFoundException(resourceType);
        }
        return permissionLevel;
    }
}
