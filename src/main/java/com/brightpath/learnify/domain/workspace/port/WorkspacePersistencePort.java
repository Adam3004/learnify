package com.brightpath.learnify.domain.workspace.port;

import com.brightpath.learnify.domain.workspace.DetailedWorkspace;
import com.brightpath.learnify.domain.workspace.Workspace;

import java.util.List;
import java.util.UUID;

public interface WorkspacePersistencePort {
    Workspace createWorkspace(String displayName, String ownerId, UUID parentWorkspaceId);

    List<Workspace> listWorkspaces(UUID parentWorkspaceId);

    Workspace getWorkspaceById(UUID workspaceId);

    DetailedWorkspace getWorkspaceDetailsById(UUID workspaceId);
}
