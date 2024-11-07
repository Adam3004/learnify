package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.PermissionsApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.FullResourcePermissionModel;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.model.PermissionDto;
import com.brightpath.learnify.model.PermissionSummaryDto;
import com.brightpath.learnify.model.ResourceFullPermissionDto;
import com.brightpath.learnify.model.ResourceTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionController implements PermissionsApi {
    private final PermissionAccessService permissionAccessService;
    private final DtoMapper dtoMapper;

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<ResourceFullPermissionDto> getFullPermissionToResource(ResourceTypeDto resourceType, UUID resourceId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        FullResourcePermissionModel permissionsAccess = permissionAccessService.getFullPermissionsForResource(resourceId, convertedResourceType);
        return ResponseEntity.ok(dtoMapper.toResourceFullPermissionDto(permissionsAccess));
    }

    @Override
    public ResponseEntity<PermissionSummaryDto> getPermissionToResourceForUser(ResourceTypeDto resourceType, UUID resourceId, String userId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        ResourceAccessEnum userAccessForResource = permissionAccessService.getUserAccessForResource(userId, resourceId,
                convertedResourceType);
        return ResponseEntity.ok(new PermissionSummaryDto(userId, resourceId, dtoMapper.toAccessTypeDto(userAccessForResource)));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<List<PermissionSummaryDto>> getPermissionsToResource(ResourceTypeDto resourceType, UUID resourceId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        List<Permission> permissionsForResource = permissionAccessService.getPermissionsForResource(resourceId, convertedResourceType);
        List<PermissionSummaryDto> permissionSummaryDtos = permissionsForResource.stream()
                .map(permission -> dtoMapper.toPermissionSummaryDto(permission, resourceId))
                .toList();
        return ResponseEntity.ok(permissionSummaryDtos);
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<PermissionSummaryDto> editPermissionToResource(ResourceTypeDto resourceType, UUID resourceId, String userId, PermissionDto permissionDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        Permission permission = permissionAccessService.editPermissionToResourceForUser(resourceId,
                convertedResourceType, userId, dtoMapper.fromAccessTypeDto(permissionDto.getAccessTypeDto()));
        return ResponseEntity.ok(dtoMapper.toPermissionSummaryDto(permission, resourceId));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<PermissionSummaryDto> setPermissionToResource(ResourceTypeDto resourceType, UUID resourceId, PermissionDto permissionDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        Permission permission = permissionAccessService.addPermissionToResourceForUser(resourceId,
                convertedResourceType, permissionDto.getUserId(), dtoMapper.fromAccessTypeDto(permissionDto.getAccessTypeDto()));
        return ResponseEntity.ok(dtoMapper.toPermissionSummaryDto(permission, resourceId));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<Void> deletePermissionToResource(ResourceTypeDto resourceType, UUID resourceId, String userId) {
        permissionAccessService.deletePermissionToResourceForUser(resourceId, userId);
        return new ResponseEntity<>(OK);
    }
}
