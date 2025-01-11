package com.brightpath.learnify.persistance.workspace;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.workspace.DetailedWorkspace;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.user.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.WORKSPACE;

@Service
@RequiredArgsConstructor
public class WorkspaceDBAdapter implements WorkspaceAdapter {
    @PersistenceContext
    private EntityManager entityManager;
    private final UuidProvider uuidProvider;
    private final WorkspaceRepository workspaceRepository;
    private final PersistentMapper persistentMapper;

    public Workspace createWorkspace(String displayName, String ownerId, UUID parentWorkspaceId) {
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        WorkspaceEntity parentWorkspace = findWorkspaceById(parentWorkspaceId);
        WorkspaceEntity workspaceEntity = new WorkspaceEntity(uuidProvider.generateUuid(), displayName, owner, new HashSet<>(), parentWorkspace);
        WorkspaceEntity result = workspaceRepository.save(workspaceEntity);
        return persistentMapper.asWorkspace(result);
    }

    public List<Workspace> listWorkspaces(UUID parentWorkspaceId) {
        List<WorkspaceEntity> workspaces;
        if (parentWorkspaceId == null) {
            workspaces = workspaceRepository.findAll();
        } else {
            workspaces = workspaceRepository.findAllWithParentWorkspace(parentWorkspaceId);
        }
        return workspaces.stream()
                .map(persistentMapper::asWorkspace)
                .toList();
    }

    public Workspace getWorkspaceById(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .map(persistentMapper::asWorkspace)
                .orElseThrow(() -> new ResourceNotFoundException(WORKSPACE));
    }

    public DetailedWorkspace getWorkspaceDetailsById(UUID workspaceId) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(WORKSPACE));
        return persistentMapper.asDetailedWorkspace(workspaceEntity);
    }

    private WorkspaceEntity findWorkspaceById(UUID workspaceId) {
        if (workspaceId == null) {
            return null;
        }
        return workspaceRepository.getReferenceById(workspaceId);
    }
}
