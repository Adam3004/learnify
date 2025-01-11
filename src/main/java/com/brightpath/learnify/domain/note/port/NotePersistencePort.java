package com.brightpath.learnify.domain.note.port;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NotePage;
import com.brightpath.learnify.domain.note.NoteType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotePersistencePort {
    @Transactional
    Note createNote(String title, String description, UUID workspaceId, String ownerId, NoteType type, PermissionLevel permissionLevel);

    void createBoardNotePage(UUID noteId, String userId);

    void createDocumentNotePage(UUID noteId, String userId);

    List<Note> searchNotes(String userId, UUID workspaceId, String ownerId, String titleFilter, PermissionLevel permissionLevel, float averageRating);

    List<Note> listRecentNotes(String userId);

    boolean noteExits(UUID noteId);

    Note getNoteById(UUID noteId, String userId);

    Optional<NotePage> getBoardNoteContentPage(UUID uuid, Integer pageNumber);

    Optional<NotePage> getDocumentNoteContentPage(UUID uuid, Integer pageNumber);

    Note updateNoteDetails(UUID noteId, UUID workspaceId, String title, String description, String userId);

    int updateBoardNoteContentPage(UUID uuid, Integer pageNumber, String body, Integer version);

    int updateDocumentNoteContentPage(UUID uuid, Integer pageNumber, String body, Integer version);

    void updateUpdatedAt(UUID noteId, String userId);

    void updateViewDateForNote(UUID noteId, String userId);

    void deleteNote(UUID noteId);
}
