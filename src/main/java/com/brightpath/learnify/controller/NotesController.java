package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.NotesApi;
import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.model.NoteCreateDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import com.brightpath.learnify.model.UserSummaryDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import com.brightpath.learnify.persistance.common.User;
import com.brightpath.learnify.persistance.common.Workspace;
import com.brightpath.learnify.domain.note.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class NotesController implements NotesApi {

    private final NoteService notesService;
    private final AuthorizationService authorizationService;

    public NotesController(NoteService notesService, AuthorizationService authorizationService) {
        this.notesService = notesService;
        this.authorizationService = authorizationService;
    }

    @Override
    public ResponseEntity<NoteSummaryDto> getNoteById(String noteId) {
        Note note = notesService.getNoteById(UUID.fromString(noteId));
        return ResponseEntity.ok(asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<NoteSummaryDto> createNote(NoteCreateDto noteCreateDto) {
        User user = authorizationService.defaultUser();
        Note note = notesService.createNote(
                noteCreateDto.getTitle(),
                noteCreateDto.getDescription(),
                UUID.fromString(noteCreateDto.getWorkspaceId()),
                user.uuid()
        );
        return ResponseEntity.ok(asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listRecentNotes() {
        List<Note> noteSummaries = notesService.listRecentNotes();
        return ResponseEntity.ok(noteSummaries.stream()
                .map(this::asNoteSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<String> updateNoteContentPage(String noteId, Integer pageNumber, String body) {
        notesService.updateNoteContentPage(UUID.fromString(noteId), pageNumber, body);
        return ResponseEntity.ok("Note updated");
    }

    @Override
    public ResponseEntity<String> getNoteContentPage(String noteId, Integer pageNumber) {
        String content = notesService.getNoteContentPage(UUID.fromString(noteId), pageNumber);
        return ResponseEntity.ok(content);
    }

    private NoteSummaryDto asNoteSummaryDto(Note note) {
        return new NoteSummaryDto()
                .id(note.uuid().toString())
                .title(note.title())
                .description(note.description())
                .workspace(asWorkspaceSummaryDto(note.workspace()))
                .author(asUserSummaryDto(note.owner()))
                .createdAt(note.createdAt())
                .updatedAt(note.updatedAt());
    }

    private UserSummaryDto asUserSummaryDto(User owner) {
        return new UserSummaryDto()
                .id(owner.uuid().toString())
                .displayName(owner.displayName());
    }

    private WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace workspace) {
        return new WorkspaceSummaryDto()
                .id(workspace.uuid().toString())
                .displayName(workspace.displayName());
    }
}
