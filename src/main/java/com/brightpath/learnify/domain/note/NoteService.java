package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.exception.conflict.ResourceUpdateConflictException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.domain.note.port.NotePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.common.ResourceType.BOARD_NOTE_PAGE;
import static com.brightpath.learnify.domain.common.ResourceType.DOCUMENT_NOTE_PAGE;
import static com.brightpath.learnify.domain.common.ResourceType.NOTE;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final PermissionAccessService permissionAccessService;
    private final BindingService bindingService;
    private final NotePersistencePort notePersistencePort;

    public Note createNote(String title, String description, UUID workspaceId, String ownerId, NoteType type, PermissionLevel permissionLevel) {
        return notePersistencePort.createNote(title, description, workspaceId, ownerId, type, permissionLevel);
    }

    public List<Note> listRecentNotes(String userId) {
        List<Note> notes = notePersistencePort.listRecentNotes(userId);
        return notes.stream()
                .filter(note -> permissionAccessService.hasUserAccessToResource(userId, note.id(), NOTE, READ_ONLY))
                .toList();
    }

    public Note getNoteById(UUID id, String userId) {
        checkIfNoteExists(id);
        return notePersistencePort.getNoteById(id, userId);
    }

    public void checkIfNoteExists(UUID id) {
        if (!notePersistencePort.noteExits(id)) {
            throw new ResourceNotFoundException(NOTE);
        }
    }

    @Transactional
    public void updateBoardNoteContentPage(UUID uuid, String userId, Integer pageNumber, String body, Integer version) {
        int rowsAffected = notePersistencePort.updateBoardNoteContentPage(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(BOARD_NOTE_PAGE, uuid);
        }
        notePersistencePort.updateUpdatedAt(uuid, userId);
    }

    @Transactional
    public void updateDocumentNoteContentPage(UUID uuid, String userId, Integer pageNumber, String body, Integer version) {
        int rowsAffected = notePersistencePort.updateDocumentNoteContentPage(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(DOCUMENT_NOTE_PAGE, uuid);
        }
        notePersistencePort.updateUpdatedAt(uuid, userId);
    }

    @Transactional
    public NotePage getBoardNoteContentPage(UUID uuid, Integer pageNumber, String userId) {
        Optional<NotePage> boardNoteContentPage = notePersistencePort.getBoardNoteContentPage(uuid, pageNumber);
        notePersistencePort.updateViewDateForNote(uuid, userId);
        return boardNoteContentPage.orElseThrow(() -> new ResourceNotFoundException(BOARD_NOTE_PAGE));
    }

    @Transactional
    public NotePage getDocumentNoteContentPage(UUID uuid, Integer pageNumber, String userId) {
        Optional<NotePage> byNoteIdAndPageNumber = notePersistencePort.getDocumentNoteContentPage(uuid, pageNumber);
        notePersistencePort.updateViewDateForNote(uuid, userId);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(DOCUMENT_NOTE_PAGE));
    }

    public void createBoardNotePage(UUID noteId, String userId) {
        notePersistencePort.createBoardNotePage(noteId, userId);
    }

    public void createDocumentNotePage(UUID noteId, String userId) {
        notePersistencePort.createDocumentNotePage(noteId, userId);
    }

    @Transactional
    public void deleteNote(UUID noteId) {
        permissionAccessService.deletePermissionToResource(noteId);
        bindingService.removeBindingForNote(noteId);
        notePersistencePort.deleteNote(noteId);
    }

    public List<Note> searchNotes(String userId, UUID workspaceId, String ownerId, String titlePart, PermissionLevel permissionLevel, float averageRating) {
        String titleFilter = Optional.ofNullable(titlePart).map(String::toLowerCase).orElse("");
        return notePersistencePort.searchNotes(userId, workspaceId, ownerId, titleFilter, permissionLevel, averageRating);
    }

    public Note updateNoteDetails(UUID noteId, UUID workspaceId, String title, String description, String userId) {
        return notePersistencePort.updateNoteDetails(noteId, workspaceId, title, description, userId);
    }
}
