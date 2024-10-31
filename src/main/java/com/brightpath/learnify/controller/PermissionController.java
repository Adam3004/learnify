package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.PermissionsApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.model.PermissionDto;
import com.brightpath.learnify.model.ResourceTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionController implements PermissionsApi {
    private final PermissionAccessService permissionAccessService;
    private final DtoMapper dtoMapper;

    @Override
    public ResponseEntity<Void> editPermissionToResource(UUID resourceId, PermissionDto permissionDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(permissionDto.getResourceTypeDto());
        permissionAccessService.checkUserPermissionToEditResource(resourceId, convertedResourceType);
        permissionAccessService.addPermissionToResourceForUser(resourceId, convertedResourceType, permissionDto.getUserId(),
                dtoMapper.fromAccessTypeDto(permissionDto.getAccessTypeDto()));
        return new ResponseEntity<>(OK);
    }

    @Override
    public ResponseEntity<Void> setPermissionToResource(UUID resourceId, PermissionDto permissionDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(permissionDto.getResourceTypeDto());
        permissionAccessService.checkUserPermissionToEditResource(resourceId, convertedResourceType);
        permissionAccessService.editPermissionToResourceForUser(resourceId, convertedResourceType, permissionDto.getUserId(),
                dtoMapper.fromAccessTypeDto(permissionDto.getAccessTypeDto()));
        return new ResponseEntity<>(OK);
    }

    @Override
    public ResponseEntity<Void> deletePermissionToResource(ResourceTypeDto resourceType, UUID resourceId, String userId) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        permissionAccessService.checkUserPermissionToEditResource(resourceId, convertedResourceType);
        permissionAccessService.deletePermissionToResourceForUser(resourceId, convertedResourceType, userId);
        return new ResponseEntity<>(OK);
    }
}
