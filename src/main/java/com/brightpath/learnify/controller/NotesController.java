package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.NotesApi;
import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.model.NoteCreateDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotesController implements NotesApi {

    private final NoteService notesService;
    private final AuthorizationService authorizationService;
    private final DtoMapper dtoMapper;

    @Override
    public ResponseEntity<NoteSummaryDto> getNoteById(String noteId) {
        Note note = notesService.getNoteById(UUID.fromString(noteId));
        return ResponseEntity.ok(dtoMapper.asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<NoteSummaryDto> createNote(NoteCreateDto noteCreateDto) {
        User user = authorizationService.defaultUser();
        Note note = notesService.createNote(
                noteCreateDto.getTitle(),
                noteCreateDto.getDescription(),
                UUID.fromString(noteCreateDto.getWorkspaceId()),
                user.id()
        );
        return ResponseEntity.ok(dtoMapper.asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listRecentNotes() {
        List<Note> noteSummaries = notesService.listRecentNotes();
        return ResponseEntity.ok(noteSummaries.stream()
                .map(dtoMapper::asNoteSummaryDto)
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
}
