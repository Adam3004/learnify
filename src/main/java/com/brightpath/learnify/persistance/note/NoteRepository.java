package com.brightpath.learnify.persistance.note;

import org.springframework.data.domain.Pageable;
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
    List<NoteEntity> findTop4ByOrderByViewedAtDesc(@Param("userId") String userId, Pageable pageable);


    @Query("""
            SELECT u
            FROM NoteEntity u
            WHERE (:workspaceId IS NULL OR u.workspace.id = :workspaceId)
            """)
    List<NoteEntity> searchNotes(UUID workspaceId);
}
