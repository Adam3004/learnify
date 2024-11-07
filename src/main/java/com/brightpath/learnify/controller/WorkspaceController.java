package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.WorkspacesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.domain.workspace.WorkspaceService;
import com.brightpath.learnify.model.WorkspaceCreateDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WorkspaceController implements WorkspacesApi {
    private final DtoMapper dtoMapper;
    private final WorkspaceService workspaceService;
    private final UserIdentityService userIdentityService;

    @Override
    public ResponseEntity<List<WorkspaceSummaryDto>> listWorkspaces() {
        //todo add do it per user
        List<Workspace> workspaces = workspaceService.listWorkspaces();

        return ResponseEntity.ok(workspaces.stream()
                .map(this::asWorkspaceSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<WorkspaceSummaryDto> createWorkspace(WorkspaceCreateDto workspaceCreateDto) {
        String userId = userIdentityService.getCurrentUserId();
        Workspace workspace = workspaceService.createWorkspace(workspaceCreateDto.getDisplayName(),
                userId,
                dtoMapper.fromResourceAccessTypeDto(workspaceCreateDto.getResourceAccessTypeDto()));

        return ResponseEntity.ok(asWorkspaceSummaryDto(workspace));
    }

    private WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace summary) {
        return new WorkspaceSummaryDto()
                .id(summary.id())
                .displayName(summary.displayName());
    }
}