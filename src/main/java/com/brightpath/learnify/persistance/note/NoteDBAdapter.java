package com.brightpath.learnify.persistance.note;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NotePage;
import com.brightpath.learnify.domain.note.NoteType;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.auth.permissions.PermissionAccessAdapter;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.common.RatingsEmbeddableEntity;
import com.brightpath.learnify.persistance.note.date.DateStatisticsEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
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

import static com.brightpath.learnify.domain.common.ResourceType.NOTE;

@Service
@RequiredArgsConstructor
public class NoteDBAdapter implements NoteAdapter {
    @PersistenceContext
    private EntityManager entityManager;
    private final NoteRepository noteRepository;
    private final BoardNotePageRepository boardNotePageRepository;
    private final DocumentNotePageRepository documentNotePageRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final PermissionAccessAdapter permissionAccessAdapter;

    @Transactional
    public Note createNote(String title, String description, UUID workspaceId, String ownerId, NoteType type, PermissionLevel permissionLevel) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        UUID noteId = uuidProvider.generateUuid();
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessAdapter.savePermissionAccess(noteId, NOTE, ownerId, permissionLevel);
        RatingsEmbeddableEntity ratings = new RatingsEmbeddableEntity(0, 0, 0);
        DateStatisticsEntity dateStatisticsEntity = new DateStatisticsEntity(uuidProvider.generateUuid(), ownerId, now, now);
        NoteEntity note = new NoteEntity(noteId, title, description, workspace, owner, now, new HashSet<>(List.of(dateStatisticsEntity)), type, 1, permissionsAccessEntity, ratings);
        NoteEntity result = noteRepository.save(note);
        switch (type) {
            case BOARD ->
                    boardNotePageRepository.save(new BoardNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, "", 1));
            case DOCUMENT ->
                    documentNotePageRepository.save(new DocumentNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, "", 1));
        }
        return persistentMapper.asNote(result, ownerId);
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

    public List<Note> searchNotes(String userId, UUID workspaceId, String ownerId, String titleFilter, PermissionLevel permissionLevel, float averageRating) {
        List<NoteEntity> notes = noteRepository.searchNotes(userId, workspaceId, ownerId, titleFilter, permissionLevel, averageRating);
        return notes.stream()
                .map(note -> persistentMapper.asNote(note, userId))
                .toList();
    }

    public List<Note> listRecentNotes(String userId) {
        Pageable pageable = PageRequest.of(0, 4);
        List<NoteEntity> notes = noteRepository.findRecentlyVisitedNotes(userId, pageable);
        return notes.stream()
                .map(note -> persistentMapper.asNote(note, userId))
                .toList();
    }

    public boolean noteExits(UUID noteId) {
        return noteRepository.findById(noteId).isPresent();
    }

    public Note getNoteById(UUID noteId, String userId) {
        NoteEntity note = noteRepository.findById(noteId).get();
        updateViewDateForNote(noteId, userId);
        return persistentMapper.asNote(note, userId);
    }

    public Optional<NotePage> getBoardNoteContentPage(UUID uuid, Integer pageNumber) {
        return boardNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
    }

    public Optional<NotePage> getDocumentNoteContentPage(UUID uuid, Integer pageNumber) {
        return documentNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
    }

    public Note updateNoteDetails(UUID noteId, UUID workspaceId, String title, String description, String userId) {
        NoteEntity noteById = noteRepository.findById(noteId).orElseThrow(() -> new ResourceNotFoundException(NOTE));
        if (workspaceId == null) {
            workspaceId = noteById.getWorkspace().getId();
        }
        if (title == null) {
            title = noteById.getTitle();
        }
        if (description == null) {
            description = noteById.getDescription();
        }
        updateUpdatedAt(noteById, userId);
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity owner = entityManager.getReference(UserEntity.class, noteById.getOwner().getId());
        NoteEntity note = new NoteEntity(noteId, title, description, workspace, owner, noteById.getCreatedAt(), noteById.getDateStatistics(), noteById.getType(), noteById.getPagesCount(), noteById.getPermissionsAccess(), noteById.getRatings());
        NoteEntity result = noteRepository.save(note);
        return persistentMapper.asNote(result, userId);
    }

    public int updateBoardNoteContentPage(UUID uuid, Integer pageNumber, String body, Integer version) {
        return boardNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body, version);
    }

    public int updateDocumentNoteContentPage(UUID uuid, Integer pageNumber, String body, Integer version) {
        return documentNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body, version);
    }

    public void updateUpdatedAt(UUID noteId, String userId) {
        NoteEntity note = noteRepository.getReferenceById(noteId);
        updateUpdatedAt(note, userId);
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

    public void updateViewDateForNote(UUID noteId, String userId) {
        NoteEntity note = noteRepository.getReferenceById(noteId);
        updateViewDateForNote(note, userId);
    }

    public void deleteNote(UUID noteId) {
        noteRepository.deleteById(noteId);
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

    private Optional<DateStatisticsEntity> getDateStatisticForUser(NoteEntity note, String userId) {
        return note.getDateStatistics().stream()
                .filter(stats -> stats.getUserId().equals(userId))
                .findAny();
    }
}
