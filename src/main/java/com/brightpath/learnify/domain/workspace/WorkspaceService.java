package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.persistance.workspace.WorkspaceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.common.ResourceType.WORKSPACE;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final PermissionAccessService permissionAccessService;
    private final WorkspaceAdapter workspaceAdapter;

    @Transactional
    public Workspace createWorkspace(String displayName, String ownerId, PermissionLevel permissionLevel, UUID parentWorkspaceId) {
        Workspace createdWorkspace = workspaceAdapter.createWorkspace(displayName, ownerId, parentWorkspaceId);
        permissionAccessService.savePermissionAccess(createdWorkspace.id(), WORKSPACE, ownerId, permissionLevel);
        return createdWorkspace;
    }

    public List<Workspace> listWorkspaces(String userId, UUID parentWorkspaceId) {
        List<Workspace> workspaces = workspaceAdapter.listWorkspaces(parentWorkspaceId);
        return workspaces.stream()
                .filter(workspace -> permissionAccessService.hasUserAccessToResource(userId, workspace.id(), WORKSPACE, READ_ONLY))
                .toList();
    }

    public Workspace getWorkspaceById(UUID workspaceId) {
        return workspaceAdapter.getWorkspaceById(workspaceId);
    }

    public DetailedWorkspace getWorkspaceDetailsById(UUID workspaceId) {
        return workspaceAdapter.getWorkspaceDetailsById(workspaceId);
    }
}
