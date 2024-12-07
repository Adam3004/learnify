package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.conflict.ResourceUpdateConflictException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.note.BoardNotePageEntity;
import com.brightpath.learnify.persistance.note.BoardNotePageRepository;
import com.brightpath.learnify.persistance.note.DocumentNotePageEntity;
import com.brightpath.learnify.persistance.note.DocumentNotePageRepository;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.note.NoteRepository;
import com.brightpath.learnify.persistance.note.date.DateStatisticsEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashSet;
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
        DateStatisticsEntity dateStatisticsEntity = new DateStatisticsEntity(uuidProvider.generateUuid(), ownerId, now, now);
        NoteEntity note = new NoteEntity(uuidProvider.generateUuid(), title, description, workspace, owner, now, new HashSet<>(List.of(dateStatisticsEntity)), type, 1);
        NoteEntity result = noteRepository.save(note);
        permissionAccessService.savePermissionAccess(note.getId(), NOTE, ownerId, permissionLevel);
        switch (type) {
            case BOARD ->
                    boardNotePageRepository.save(new BoardNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, "", 1));
            case DOCUMENT ->
                    documentNotePageRepository.save(new DocumentNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, "", 1));
        }
        return persistentMapper.asNote(result, ownerId);
    }

    public List<Note> listRecentNotes(String userId) {
        Pageable pageable = PageRequest.of(0, 4);
        List<NoteEntity> notes = noteRepository.findTop4ByOrderByViewedAtDesc(userId, pageable);
        return notes.stream()
                .map(note -> persistentMapper.asNote(note, userId))
                .filter(note -> permissionAccessService.hasUserAccessToResource(userId, note.id(), NOTE, READ_ONLY))
                .toList();
    }

    public Note getNoteById(UUID uuid, String userId, boolean shouldUpdateViewedDate) {
        Optional<NoteEntity> note = noteRepository.findById(uuid);
        if (note.isEmpty()) {
            throw new ResourceNotFoundException(NOTE);
        }
        if (shouldUpdateViewedDate) {
            updateViewDateForNote(note.get(), userId);
        }
        return persistentMapper.asNote(note.get(), userId);
    }

    private void updateViewDateForNote(NoteEntity note, String userId) {
        Optional<DateStatisticsEntity> dateStatisticForUser = getDateStatisticForUser(note, userId);
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        if (dateStatisticForUser.isPresent()) {
            dateStatisticForUser.get().setViewedAt(now);
        } else {
            note.getDateStatistics().add(new DateStatisticsEntity(uuidProvider.generateUuid(), userId, null, now));
        }
    }

    @Transactional
    public void updateBoardNoteContentPage(UUID uuid, String userId, Integer pageNumber, String body, Integer version) {
        int rowsAffected = boardNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(BOARD_NOTE_PAGE, uuid);
        }
        updateUpdatedAt(noteRepository.getReferenceById(uuid), userId);
    }

    @Transactional
    public void updateDocumentNoteContentPage(UUID uuid, String userId, Integer pageNumber, String body, Integer version) {
        int rowsAffected = documentNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body, version);
        if (rowsAffected == 0) {
            throw new ResourceUpdateConflictException(DOCUMENT_NOTE_PAGE, uuid);
        }
        updateUpdatedAt(noteRepository.getReferenceById(uuid), userId);
    }

    public NotePage getBoardNoteContentPage(UUID uuid, Integer pageNumber, String userId) {
        Optional<NotePage> byNoteIdAndPageNumber = boardNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        updateViewDateForNote(noteRepository.getReferenceById(uuid), userId);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(BOARD_NOTE_PAGE));
    }

    public NotePage getDocumentNoteContentPage(UUID uuid, Integer pageNumber, String userId) {
        Optional<NotePage> byNoteIdAndPageNumber = documentNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        updateViewDateForNote(noteRepository.getReferenceById(uuid), userId);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(DOCUMENT_NOTE_PAGE));
    }

    public List<Note> searchNotes(String userId, @Nullable UUID workspaceId) {
        List<NoteEntity> notes = noteRepository.searchNotes(workspaceId);
        return notes.stream()
                .map(note -> persistentMapper.asNote(note, userId))
                .filter(note -> permissionAccessService.hasUserAccessToResource(userId, note.id(), NOTE, READ_ONLY))
                .toList();
    }

    public void createBoardNotePage(UUID noteId, String userId) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(NOTE));
        int newPageNumber = note.getPagesCount() + 1;
        boardNotePageRepository.save(new BoardNotePageEntity(uuidProvider.generateUuid(), noteId, newPageNumber, "", 1));
        note.setPagesCount(newPageNumber);
        updateUpdatedAt(note, userId);
        noteRepository.save(note);
    }

    public void createDocumentNotePage(UUID noteId, String userId) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(NOTE));
        int newPageNumber = note.getPagesCount() + 1;
        documentNotePageRepository.save(new DocumentNotePageEntity(uuidProvider.generateUuid(), noteId, newPageNumber, "", 1));
        note.setPagesCount(newPageNumber);
        updateUpdatedAt(note, userId);
        noteRepository.save(note);
    }

    private void updateUpdatedAt(NoteEntity note, String userId) {
        Optional<DateStatisticsEntity> foundStatistics = getDateStatisticForUser(note, userId);
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        if (foundStatistics.isPresent()) {
            DateStatisticsEntity dateStatisticsEntity = foundStatistics.get();
            dateStatisticsEntity.setUpdatedAt(now);
            dateStatisticsEntity.setViewedAt(now);
        } else {
            note.getDateStatistics().add(new DateStatisticsEntity(uuidProvider.generateUuid(), userId, now, now));
        }
    }

    private Optional<DateStatisticsEntity> getDateStatisticForUser(NoteEntity note, String userId) {
        return note.getDateStatistics().stream()
                .filter(stats -> stats.getUserId().equals(userId))
                .findAny();
    }

    @Transactional
    public void deleteNote(UUID noteId) {
        noteRepository.deleteById(noteId);
        permissionAccessService.deletePermissionToResource(noteId);
        bindingService.removeBindingForNote(noteId);
        noteRepository.deleteById(noteId);
    }
}
