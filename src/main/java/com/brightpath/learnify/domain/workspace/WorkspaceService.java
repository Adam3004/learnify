package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
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

    public Workspace createWorkspace(String displayName, String ownerId, PermissionLevel permissionLevel) {
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        WorkspaceEntity workspaceEntity = new WorkspaceEntity(uuidProvider.generateUuid(), displayName, owner);
        WorkspaceEntity result = workspaceRepository.save(workspaceEntity);
        permissionAccessService.savePermissionAccess(result.getId(), WORKSPACE, ownerId, permissionLevel);
        return persistentMapper.asWorkspace(result);
    }

    public List<Workspace> listWorkspaces(String userId) {
        List<WorkspaceEntity> workspaces = workspaceRepository.findAll();
        return workspaces.stream()
                .map(persistentMapper::asWorkspace)
                .filter(workspace -> permissionAccessService.hasUserAccessToResource(userId, workspace.id(), WORKSPACE, READ_ONLY))
                .toList();
    }

    public Workspace getWorkspaceById(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .map(persistentMapper::asWorkspace)
                .orElseThrow(() -> new ResourceNotFoundException(WORKSPACE));
    }
}
