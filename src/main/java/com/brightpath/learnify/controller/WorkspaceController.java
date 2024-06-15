package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.WorkspacesApi;
import com.brightpath.learnify.domain.workspace.WorkspaceService;
import com.brightpath.learnify.model.WorkspaceCreateDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import com.brightpath.learnify.persistance.common.Workspace;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WorkspaceController implements WorkspacesApi {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.init();
    }

    private void init() {
        workspaceService.createWorkspace("Semestr 1");
        workspaceService.createWorkspace("Semestr 2");
        workspaceService.createWorkspace("Semestr 3");
        workspaceService.createWorkspace("Semestr 4");
    }

    @Override
    public ResponseEntity<List<WorkspaceSummaryDto>> listWorkspaces() {
        var workspaces = workspaceService.listWorkspaces();

        return ResponseEntity.ok(workspaces.stream()
                .map(this::asWorkspaceSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<WorkspaceSummaryDto> createWorkspace(WorkspaceCreateDto workspaceCreateDto) {
        var workspace = workspaceService.createWorkspace(workspaceCreateDto.getDisplayName());

        return ResponseEntity.ok(asWorkspaceSummaryDto(workspace));
    }

    private WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace summary) {
        return new WorkspaceSummaryDto()
                .id(summary.uuid().toString())
                .displayName(summary.displayName());
    }
}