package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.PermissionsApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.model.PermissionDto;
import com.brightpath.learnify.model.PermissionSummaryDto;
import com.brightpath.learnify.model.ResourceTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PermissionSummaryDto> getPermissionToResourceForUser(ResourceTypeDto resourceType, UUID resourceId, String userId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        permissionAccessService.checkUserPermissionToViewResource(resourceId, convertedResourceType);
        ResourceAccessEnum userAccessForResource = permissionAccessService.getUserAccessForResource(userId, resourceId,
                convertedResourceType);
        return ResponseEntity.ok(new PermissionSummaryDto(userId, resourceId, dtoMapper.toAccessTypeDto(userAccessForResource)));
    }

    @Override
    public ResponseEntity<List<PermissionSummaryDto>> getPermissionsToResource(ResourceTypeDto resourceType, UUID resourceId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        permissionAccessService.checkUserPermissionToViewResource(resourceId, convertedResourceType);
        List<Permission> permissionsForResource = permissionAccessService.getPermissionsForResource(resourceId, convertedResourceType);
        List<PermissionSummaryDto> permissionSummaryDtos = permissionsForResource.stream()
                .map(permission -> new PermissionSummaryDto(
                        permission.userId(),
                        resourceId,
                        dtoMapper.toAccessTypeDto(permission.resourceAccessEnum())
                ))
                .toList();
        return ResponseEntity.ok(permissionSummaryDtos);
    }

    @Override
    public ResponseEntity<PermissionSummaryDto> editPermissionToResource(ResourceTypeDto resourceType, UUID resourceId, String userId, PermissionDto permissionDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        permissionAccessService.checkUserPermissionToEditResource(resourceId, convertedResourceType);
        PermissionSummaryDto permissionSummaryDto = permissionAccessService.addPermissionToResourceForUser(resourceId,
                convertedResourceType, userId, dtoMapper.fromAccessTypeDto(permissionDto.getAccessTypeDto()));
        return ResponseEntity.ok(permissionSummaryDto);
    }

    @Override
    public ResponseEntity<PermissionSummaryDto> setPermissionToResource(ResourceTypeDto resourceType, UUID resourceId, PermissionDto permissionDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        permissionAccessService.checkUserPermissionToEditResource(resourceId, convertedResourceType);
        PermissionSummaryDto permissionSummaryDto = permissionAccessService.editPermissionToResourceForUser(resourceId,
                convertedResourceType, permissionDto.getUserId(), dtoMapper.fromAccessTypeDto(permissionDto.getAccessTypeDto()));
        return ResponseEntity.ok(permissionSummaryDto);
    }

    @Override
    public ResponseEntity<Void> deletePermissionToResource(ResourceTypeDto resourceType, UUID resourceId, String userId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        permissionAccessService.checkUserPermissionToEditResource(resourceId, convertedResourceType);
        permissionAccessService.deletePermissionToResourceForUser(resourceId, convertedResourceType, userId);
        return new ResponseEntity<>(OK);
    }
}
