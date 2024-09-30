package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.note.NotePageEntity;
import com.brightpath.learnify.persistance.note.NotePageRepository;
import com.brightpath.learnify.persistance.note.NoteRepository;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteService {

    @PersistenceContext
    private EntityManager entityManager;
    private final NoteRepository noteRepository;
    private final NotePageRepository notePageRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public NoteService(NoteRepository noteRepository, NotePageRepository notePageRepository, PersistentMapper persistentMapper, UuidProvider uuidProvider) {
        this.noteRepository = noteRepository;
        this.notePageRepository = notePageRepository;
        this.persistentMapper = persistentMapper;
        this.uuidProvider = uuidProvider;
    }

    public Note createNote(String title, String description, UUID workspaceId, UUID ownerId, NoteType type) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity owner = entityManager.getReference(UserEntity.class, ownerId);
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        NoteEntity note = new NoteEntity(uuidProvider.generateUuid(), title, description, workspace, owner, now, now, type);
        NoteEntity result = noteRepository.save(note);
        notePageRepository.save(new NotePageEntity(uuidProvider.generateUuid(), result.getId(), 1, ""));
        return persistentMapper.asNote(result);
    }

    public List<Note> listRecentNotes() {
        List<NoteEntity> notes = noteRepository.findTop4ByOrderByUpdatedAtDesc();
        return notes.stream()
                .map(persistentMapper::asNote)
                .toList();
    }

    public Note getNoteById(UUID uuid) {
        Optional<NoteEntity> note = noteRepository.findById(uuid);
        if (note.isEmpty()) {
            throw new IllegalArgumentException("Note not found");
        }
        return persistentMapper.asNote(note.get());
    }

    @Transactional
    public void updateNoteContentPage(UUID uuid, Integer pageNumber, String body) {
        notePageRepository.updateByNoteIdAndPageNumber(uuid, pageNumber, body);
    }

    public String getNoteContentPage(UUID uuid, Integer pageNumber) {
        Optional<String> byNoteIdAndPageNumber = notePageRepository.findByNoteIdAndPageNumber(uuid, pageNumber);
        return byNoteIdAndPageNumber.orElseThrow(() -> new IllegalArgumentException("Note page not found"));
    }
}
