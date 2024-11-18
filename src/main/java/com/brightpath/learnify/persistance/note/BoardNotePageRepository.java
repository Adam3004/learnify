package com.brightpath.learnify.persistance.note;

import com.brightpath.learnify.domain.note.NotePage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardNotePageRepository extends JpaRepository<BoardNotePageEntity, UUID> {
    @Query("SELECT new com.brightpath.learnify.domain.note.NotePage(pageNumber, content, version) FROM BoardNotePageEntity WHERE noteId = ?1 AND pageNumber = ?2")
    Optional<NotePage> findByNoteIdAndPageNumber(UUID noteId, int pageNumber);

    @Modifying
    @Query("UPDATE BoardNotePageEntity SET content = ?3, version = version + 1 WHERE noteId = ?1 AND pageNumber = ?2 AND version = ?4")
    int updateByNoteIdAndPageNumber(UUID noteId, int pageNumber, String content, int version);
}
