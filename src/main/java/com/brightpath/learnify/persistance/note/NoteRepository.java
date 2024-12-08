package com.brightpath.learnify.persistance.note;

import org.springframework.data.domain.Pageable;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, UUID> {
        @Query("""
            SELECT u
            FROM NoteEntity u
            ORDER BY COALESCE((SELECT ds.viewedAt FROM u.dateStatistics as ds WHERE ds.userId=:userId), u.createdAt)
            DESC
            """)
    List<NoteEntity> findRecentlyVisitedNotes(@Param("userId") String userId, Pageable pageable);


    @Query("""
            SELECT u
            FROM NoteEntity u
            JOIN PermissionsAccessEntity access ON u.id = access.resourceId
            WHERE
                (:workspaceId IS NULL OR u.workspace.id = :workspaceId)
                AND (:ownerId IS NULL OR u.owner.id = :ownerId)
                AND (:titlePart IS NULL OR lower(u.title) LIKE %:titlePart%)
                AND (:permissionLevel IS NULL OR access.permissionLevel = :permissionLevel)
                AND (
                    (access.permissionLevel = 1)
                    OR (u.owner.id = :userId)
                    OR (:userId IN (SELECT user.userId FROM access.permissions user))
                )
            """)
    List<NoteEntity> searchNotes(String userId, UUID workspaceId, String ownerId, String titlePart, PermissionLevel permissionLevel);

}
