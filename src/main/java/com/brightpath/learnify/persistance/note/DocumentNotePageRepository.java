package com.brightpath.learnify.persistance.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentNotePageRepository extends JpaRepository<DocumentNotePageEntity, UUID> {
    @Query("SELECT content FROM DocumentNotePageEntity WHERE noteId = ?1 AND pageNumber = ?2")
    Optional<String> findByNoteIdAndPageNumber(UUID noteId, int pageNumber);

    @Modifying
    @Query("UPDATE DocumentNotePageEntity SET content = ?3 WHERE noteId = ?1 AND pageNumber = ?2")
    void updateByNoteIdAndPageNumber(UUID noteId, int pageNumber, String content);
}
