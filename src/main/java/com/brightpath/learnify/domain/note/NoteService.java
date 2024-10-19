package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
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

import static com.brightpath.learnify.exception.notfound.ResourceType.DOCUMENT_NOTE_PAGE;
import static com.brightpath.learnify.exception.notfound.ResourceType.BOARD_NOTE_PAGE;
import static com.brightpath.learnify.exception.notfound.ResourceType.NOTE;

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

    public Note createNote(String title, String description, UUID workspaceId, UUID ownerId, NoteType type) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        NoteEntity note = new NoteEntity(uuidProvider.generateUuid(), title, description, workspace, owner, now, now, type);
        NoteEntity result = noteRepository.save(note);
        switch (type) {
            case BOARD ->
                    boardNotePageRepository.save(new BoardNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, ""));
            case DOCUMENT ->
                    documentNotePageRepository.save(new DocumentNotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, ""));
        }
        return persistentMapper.asNote(result);
    }

    public List<Note> listRecentNotes() {
        List<NoteEntity> notes = noteRepository.findTop4ByOrderByUpdatedAtDesc();
        return notes.stream().map(persistentMapper::asNote).toList();
    }

    public Note getNoteById(UUID uuid) {
        Optional<NoteEntity> note = noteRepository.findById(uuid);
        if (note.isEmpty()) {
            throw new ResourceNotFoundException(NOTE);
        }
        return persistentMapper.asNote(note.get());
    }

    @Transactional
    public void updateBoardNoteContentPage(UUID uuid, Integer pageNumber, String body) {
        boardNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body);
    }

    @Transactional
    public void updateDocumentNoteContentPage(UUID uuid, Integer pageNumber, String body) {
        documentNotePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body);
    }

    public String getBoardNoteContentPage(UUID uuid, Integer pageNumber) {
        Optional<String> byNoteIdAndPageNumber = boardNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(BOARD_NOTE_PAGE));
    }

    public String getDocumentNoteContentPage(UUID uuid, Integer pageNumber) {
        Optional<String> byNoteIdAndPageNumber = documentNotePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        return byNoteIdAndPageNumber.orElseThrow(() -> new ResourceNotFoundException(DOCUMENT_NOTE_PAGE));
    }

    public Note updateNoteDetails(UUID noteId, UUID workspaceId, String title, String description) {
        Note noteById = getNoteById(noteId);
        if (workspaceId == null) {
            workspaceId = noteById.workspace().id();
        }
        if (title == null) {
            title = noteById.title();
        }
        if (description == null) {
            description = noteById.description();
        }
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity owner = entityManager.getReference(UserEntity.class, noteById.owner().id());
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        NoteEntity note = new NoteEntity(noteId, title, description, workspace, owner, noteById.createdAt(), now, noteById.type());
        NoteEntity result = noteRepository.save(note);
        return persistentMapper.asNote(result);
    }
}
