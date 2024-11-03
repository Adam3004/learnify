package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.brightpath.learnify.domain.common.ResourceType.WORKSPACE;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UuidProvider uuidProvider;
    private final WorkspaceRepository workspaceRepository;
    private final PersistentMapper persistentMapper;
    private final PermissionAccessService permissionAccessService;

    public Workspace createWorkspace(String displayName, String ownerId) {
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        WorkspaceEntity workspaceEntity = new WorkspaceEntity(uuidProvider.generateUuid(), displayName, owner);
        WorkspaceEntity result = workspaceRepository.save(workspaceEntity);
        permissionAccessService.saveDefaultPermissionAccess(result.getId(), WORKSPACE, ownerId);
        return persistentMapper.asWorkspace(result);
    }

    public List<Workspace> listWorkspaces() {
        List<WorkspaceEntity> workspaces = workspaceRepository.findAll();
        return workspaces.stream()
                .map(persistentMapper::asWorkspace)
                .toList();
    }
}
