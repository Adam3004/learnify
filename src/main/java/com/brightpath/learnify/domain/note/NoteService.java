package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.exception.conflict.ResourceUpdateConflictException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.note.NoteAdapter;
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
    private final NoteAdapter noteAdapter;

    public Note createNote(String title, String description, UUID workspaceId, String ownerId, NoteType type, PermissionLevel permissionLevel) {
        return noteAdapter.createNote(title, description, workspaceId, ownerId, type, permissionLevel);
    }

    public List<Note> listRecentNotes(String userId) {
        List<Note> notes = noteAdapter.listRecentNotes(userId);
        return notes.stream()
                .filter(note -> permissionAccessService.hasUserAccessToResource(userId, note.id(), NOTE, READ_ONLY))
                .toList();
    }

    public Note getNoteById(UUID id, String userId) {
        checkIfNoteExists(id);
        return noteAdapter.getNoteById(id, userId);
    }

    public void checkIfNoteExists(UUID id) {
        if (!noteAdapter.noteExits(id)) {
            throw new ResourceNotFoundException(NOTE);
        }
    }

    @Transactional
    public void updateBoardNoteContentPage(UUID uuid, String userId, Integer pageNumber, String body, Integer version) {
        int rowsAffected = noteAdapter.updateBoardNoteContentPage(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(BOARD_NOTE_PAGE, uuid);
        }
        noteAdapter.updateUpdatedAt(uuid, userId);
    }

    @Transactional
    public void updateDocumentNoteContentPage(UUID uuid, String userId, Integer pageNumber, String body, Integer version) {
        int rowsAffected = noteAdapter.updateDocumentNoteContentPage(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(DOCUMENT_NOTE_PAGE, uuid);
        }
        noteAdapter.updateUpdatedAt(uuid, userId);
    }

    @Transactional
    public NotePage getBoardNoteContentPage(UUID uuid, Integer pageNumber, String userId) {
        Optional<NotePage> boardNoteContentPage = noteAdapter.getBoardNoteContentPage(uuid, pageNumber);
        noteAdapter.updateViewDateForNote(uuid, userId);
        return boardNoteContentPage.orElseThrow(() -> new ResourceNotFoundException(BOARD_NOTE_PAGE));
    }

    @Transactional
    public NotePage getDocumentNoteContentPage(UUID uuid, Integer pageNumber, String userId) {
        Optional<NotePage> byNoteIdAndPageNumber = noteAdapter.getDocumentNoteContentPage(uuid, pageNumber);
        noteAdapter.updateViewDateForNote(uuid, userId);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(DOCUMENT_NOTE_PAGE));
    }

    public void createBoardNotePage(UUID noteId, String userId) {
        noteAdapter.createBoardNotePage(noteId, userId);
    }

    public void createDocumentNotePage(UUID noteId, String userId) {
        noteAdapter.createDocumentNotePage(noteId, userId);
    }

    @Transactional
    public void deleteNote(UUID noteId) {
        permissionAccessService.deletePermissionToResource(noteId);
        bindingService.removeBindingForNote(noteId);
        noteAdapter.deleteNote(noteId);
    }

    public List<Note> searchNotes(String userId, UUID workspaceId, String ownerId, String titlePart, PermissionLevel permissionLevel, float averageRating) {
        String titleFilter = Optional.ofNullable(titlePart).map(String::toLowerCase).orElse("");
        return noteAdapter.searchNotes(userId, workspaceId, ownerId, titleFilter, permissionLevel, averageRating);
    }

    public Note updateNoteDetails(UUID noteId, UUID workspaceId, String title, String description, String userId) {
        return noteAdapter.updateNoteDetails(noteId, workspaceId, title, description, userId);
    }
}
