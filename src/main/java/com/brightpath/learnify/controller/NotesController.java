package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.NotesApi;
import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.model.BoardNotePageDto;
import com.brightpath.learnify.model.DocumentNotePageDto;
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
                user.id(),
                dtoMapper.asNoteType(noteCreateDto.getType())
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
    public ResponseEntity<BoardNotePageDto> getBoardNotePage(String noteId, Integer pageNumber) {
        String content = notesService.getBoardNoteContentPage(UUID.fromString(noteId), pageNumber);
        return ResponseEntity.ok(dtoMapper.asBoardNotePageContentDto(content));
    }

    @Override
    public ResponseEntity<DocumentNotePageDto> getDocumentNotePage(String noteId, Integer pageNumber) {
        String content = notesService.getDocumentNoteContentPage(UUID.fromString(noteId), pageNumber);
        return ResponseEntity.ok(dtoMapper.asDocumentNotePageDto(content));
    }

    @Override
    public ResponseEntity<String> updateBoardNotePage(String noteId, Integer pageNumber, BoardNotePageDto boardNotePageDto) {
        notesService.updateBoardNoteContentPage(UUID.fromString(noteId), pageNumber, boardNotePageDto.getContent());
        return ResponseEntity.ok("Note updated");
    }

    @Override
    public ResponseEntity<String> updateDocumentNotePage(String noteId, Integer pageNumber, DocumentNotePageDto documentNotePageDto) {
        notesService.updateBoardNoteContentPage(UUID.fromString(noteId), pageNumber, documentNotePageDto.getContent());
        return ResponseEntity.ok("Note updated");
    }
}
