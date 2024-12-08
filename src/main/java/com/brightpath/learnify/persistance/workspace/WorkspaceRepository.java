package com.brightpath.learnify.persistance.workspace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, UUID> {
    @Query("""
            SELECT w FROM WorkspaceEntity as w
            WHERE w.parentWorkspace IS NOT NULL
            AND w.parentWorkspace.id = :parentWorkspaceId
            """)
    List<WorkspaceEntity> findAllWithParentWorkspace(UUID parentWorkspaceId);
}
