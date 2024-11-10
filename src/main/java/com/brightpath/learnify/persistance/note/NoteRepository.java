package com.brightpath.learnify.persistance.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, UUID> {
    List<NoteEntity> findTop4ByOrderByUpdatedAtDesc();

    @Query("""
            SELECT u
            FROM NoteEntity u
            WHERE (:workspaceId IS NULL OR u.workspace.id = :workspaceId)
            """)
    List<NoteEntity> searchNotes(UUID workspaceId);
}
