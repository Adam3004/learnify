package com.brightpath.learnify.persistance.workspace;

import com.brightpath.learnify.domain.workspace.DetailedWorkspace;
import com.brightpath.learnify.domain.workspace.Workspace;

import java.util.List;
import java.util.UUID;

public interface WorkspaceAdapter {
    Workspace createWorkspace(String displayName, String ownerId, UUID parentWorkspaceId);

    List<Workspace> listWorkspaces(UUID parentWorkspaceId);

    Workspace getWorkspaceById(UUID workspaceId);

    DetailedWorkspace getWorkspaceDetailsById(UUID workspaceId);
}
