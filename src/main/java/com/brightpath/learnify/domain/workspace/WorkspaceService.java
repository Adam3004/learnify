package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkspaceService {

    private final UuidProvider uuidProvider;
    private final WorkspaceRepository workspaceRepository;
    private final PersistentMapper persistentMapper;

    public WorkspaceService(UuidProvider uuidProvider, WorkspaceRepository workspaceRepository, PersistentMapper persistentMapper) {
        this.uuidProvider = uuidProvider;
        this.workspaceRepository = workspaceRepository;
        this.persistentMapper = persistentMapper;
    }

    public Workspace createWorkspace(String displayName) {
        WorkspaceEntity workspaceEntity = createWorkspaceEntity(displayName);
        WorkspaceEntity result = workspaceRepository.save(workspaceEntity);
        return persistentMapper.asWorkspace(result);
    }

    public List<Workspace> listWorkspaces() {
        List<WorkspaceEntity> workspaces = workspaceRepository.findAll();
        return workspaces.stream()
                .map(persistentMapper::asWorkspace)
                .toList();
    }

    private WorkspaceEntity createWorkspaceEntity(String displayName) {
        return new WorkspaceEntity(uuidProvider.generateUuid(), displayName);
    }
}
