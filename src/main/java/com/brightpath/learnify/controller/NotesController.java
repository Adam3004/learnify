package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.NotesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NotePage;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.model.BoardNotePageDto;
import com.brightpath.learnify.model.DocumentNotePageDto;
import com.brightpath.learnify.model.NoteCreateDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotesController implements NotesApi {
    private final NoteService notesService;
    private final UserIdentityService userIdentityService;
    private final DtoMapper dtoMapper;

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<NoteSummaryDto> getNoteById(UUID noteId) {
        Note note = notesService.getNoteById(noteId);
        return ResponseEntity.ok(dtoMapper.asNoteSummaryDto(note));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#noteCreateDto.workspaceId, 'WORKSPACE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<NoteSummaryDto> createNote(NoteCreateDto noteCreateDto) {
        String userId = userIdentityService.getCurrentUserId();
        Note note = notesService.createNote(
                noteCreateDto.getTitle(),
                noteCreateDto.getDescription(),
                noteCreateDto.getWorkspaceId(),
                userId,
                dtoMapper.asNoteType(noteCreateDto.getType()),
                dtoMapper.fromResourceAccessTypeDto(noteCreateDto.getResourceAccessTypeDto())
        );
        return ResponseEntity.ok(dtoMapper.asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listRecentNotes() {
        String userId = userIdentityService.getCurrentUserId();
        List<Note> noteSummaries = notesService.listRecentNotes(userId);
        return ResponseEntity.ok(noteSummaries.stream()
                .map(dtoMapper::asNoteSummaryDto)
                .toList());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<BoardNotePageDto> getBoardNotePage(UUID noteId, Integer pageNumber) {
        NotePage page = notesService.getBoardNoteContentPage(noteId, pageNumber);
        return ResponseEntity.ok(dtoMapper.asBoardNotePageContentDto(page));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<DocumentNotePageDto> getDocumentNotePage(UUID noteId, Integer pageNumber) {
        NotePage page = notesService.getDocumentNoteContentPage(noteId, pageNumber);
        return ResponseEntity.ok(dtoMapper.asDocumentNotePageDto(page));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<String> updateBoardNotePage(UUID noteId, Integer pageNumber, BoardNotePageDto boardNotePageDto) {
        notesService.updateBoardNoteContentPage(noteId, pageNumber, boardNotePageDto.getContent(), boardNotePageDto.getVersion());
        return ResponseEntity.ok("Note updated");
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<String> updateDocumentNotePage(UUID noteId, Integer pageNumber, DocumentNotePageDto documentNotePageDto) {
        notesService.updateDocumentNoteContentPage(noteId, pageNumber, documentNotePageDto.getContent(), documentNotePageDto.getVersion());
        return ResponseEntity.ok("Note updated");
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listNotes(UUID workspaceId) {
        String userId = userIdentityService.getCurrentUserId();
        List<Note> notes = notesService.searchNotes(userId, workspaceId);
        return ResponseEntity.ok(notes.stream()
                .map(dtoMapper::asNoteSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<String> createBoardNotePage(UUID noteId) {
        notesService.createBoardNotePage(noteId);
        return ResponseEntity.ok("Note page created");
    }

    @Override
    public ResponseEntity<String> createDocumentNotePage(UUID noteId) {
        notesService.createDocumentNotePage(noteId);
        return ResponseEntity.ok("Note page created");
    }
}
