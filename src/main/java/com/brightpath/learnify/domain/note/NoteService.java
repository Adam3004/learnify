package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.conflict.ResourceUpdateConflictException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.note.BoardNotePageEntity;
import com.brightpath.learnify.persistance.note.BoardNotePageRepository;
import com.brightpath.learnify.persistance.note.DocumentNotePageEntity;
import com.brightpath.learnify.persistance.note.DocumentNotePageRepository;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.note.NoteRepository;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
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
    @PersistenceContext
    private EntityManager entityManager;
    private final NoteRepository noteRepository;
    private final BoardNotePageRepository boardNotePageRepository;
    private final DocumentNotePageRepository documentNotePageRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final PermissionAccessService permissionAccessService;
    private final BindingService bindingService;

    @Transactional
    public Note createNote(String title, String description, UUID workspaceId, String ownerId, NoteType type, PermissionLevel permissionLevel) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        UUID noteId = uuidProvider.generateUuid();
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessService.savePermissionAccess(noteId, NOTE, ownerId, permissionLevel);
        NoteEntity note = new NoteEntity(noteId, title, description, workspace, owner, now, now, type, 1, permissionsAccessEntity);
        NoteEntity result = noteRepository.save(note);
        switch (type) {
            case BOARD ->
                    boardNotePageRepository.save(new BoardNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, "", 1));
            case DOCUMENT ->
                    documentNotePageRepository.save(new DocumentNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, "", 1));
        }
        return persistentMapper.asNote(result);
    }

    public List<Note> listRecentNotes(String userId) {
        List<NoteEntity> notes = noteRepository.findTop4ByOrderByUpdatedAtDesc();
        return notes.stream()
                .map(persistentMapper::asNote)
                .filter(note -> permissionAccessService.hasUserAccessToResource(userId, note.id(), NOTE, READ_ONLY))
                .toList();
    }

    public Note getNoteById(UUID uuid) {
        Optional<NoteEntity> note = noteRepository.findById(uuid);
        if (note.isEmpty()) {
            throw new ResourceNotFoundException(NOTE);
        }
        return persistentMapper.asNote(note.get());
    }

    @Transactional
    public void updateBoardNoteContentPage(UUID uuid, Integer pageNumber, String body, Integer version) {
        int rowsAffected = boardNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(BOARD_NOTE_PAGE, uuid);
        }
    }

    @Transactional
    public void updateDocumentNoteContentPage(UUID uuid, Integer pageNumber, String body, Integer version) {
        int rowsAffected = documentNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(DOCUMENT_NOTE_PAGE, uuid);
        }
    }

    public NotePage getBoardNoteContentPage(UUID uuid, Integer pageNumber) {
        Optional<NotePage> byNoteIdAndPageNumber = boardNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(BOARD_NOTE_PAGE));
    }

    public NotePage getDocumentNoteContentPage(UUID uuid, Integer pageNumber) {
        Optional<NotePage> byNoteIdAndPageNumber = documentNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(DOCUMENT_NOTE_PAGE));
    }

    public void createBoardNotePage(UUID noteId) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(NOTE));
        int newPageNumber = note.getPagesCount() + 1;
        boardNotePageRepository.save(new BoardNotePageEntity(uuidProvider.generateUuid(), noteId, newPageNumber, "", 1));
        note.setPagesCount(newPageNumber);
        note.setUpdatedAt(OffsetDateTime.now(Clock.systemUTC()));
        noteRepository.save(note);
    }

    public void createDocumentNotePage(UUID noteId) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(NOTE));
        int newPageNumber = note.getPagesCount() + 1;
        documentNotePageRepository.save(new DocumentNotePageEntity(uuidProvider.generateUuid(), noteId, newPageNumber, "", 1));
        note.setPagesCount(newPageNumber);
        note.setUpdatedAt(OffsetDateTime.now(Clock.systemUTC()));
        noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(UUID noteId) {
        noteRepository.deleteById(noteId);
        permissionAccessService.deletePermissionToResource(noteId);
        bindingService.removeBindingForNote(noteId);
        noteRepository.deleteById(noteId);
    }

    public List<Note> searchNotes(String userId, UUID workspaceId, String ownerId, String titlePart, PermissionLevel permissionLevel) {
        String titleFilter = Optional.ofNullable(titlePart).map(String::toLowerCase).orElse("");
        List<NoteEntity> notes = noteRepository.searchNotes(userId, workspaceId, ownerId, titleFilter, permissionLevel);
        return notes.stream()
                .map(persistentMapper::asNote)
                .toList();
    }
}
