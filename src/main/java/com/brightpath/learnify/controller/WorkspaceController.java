package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.WorkspacesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.workspace.DetailedWorkspace;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.domain.workspace.WorkspaceService;
import com.brightpath.learnify.model.WorkspaceCreateDto;
import com.brightpath.learnify.model.WorkspaceDetailsDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WorkspaceController implements WorkspacesApi {
    private final DtoMapper dtoMapper;
    private final WorkspaceService workspaceService;
    private final UserIdentityService userIdentityService;

    @Override
    public ResponseEntity<List<WorkspaceSummaryDto>> listWorkspaces(UUID parentWorkspaceId) {
        String userId = userIdentityService.getCurrentUserId();
        List<Workspace> workspaces = workspaceService.listWorkspaces(userId, parentWorkspaceId);

        return ResponseEntity.ok(workspaces.stream()
                .map(dtoMapper::asWorkspaceSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<WorkspaceSummaryDto> createWorkspace(WorkspaceCreateDto workspaceCreateDto) {
        String userId = userIdentityService.getCurrentUserId();
        Workspace workspace = workspaceService.createWorkspace(workspaceCreateDto.getDisplayName(),
                userId,
                dtoMapper.fromResourceAccessTypeDto(workspaceCreateDto.getResourceAccessTypeDto()),
                workspaceCreateDto.getParentWorkspaceId());

        return ResponseEntity.ok(dtoMapper.asWorkspaceSummaryDto(workspace));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#workspaceId, 'WORKSPACE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<WorkspaceSummaryDto> getWorkspaceById(UUID workspaceId) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);

        return ResponseEntity.ok(dtoMapper.asWorkspaceSummaryDto(workspace));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#workspaceId, 'WORKSPACE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<WorkspaceDetailsDto> getWorkspaceDetailsById(UUID workspaceId) {
        DetailedWorkspace workspace = workspaceService.getWorkspaceDetailsById(workspaceId);

        return ResponseEntity.ok(dtoMapper.asWorkspaceDetailsDto(workspace));
    }
}