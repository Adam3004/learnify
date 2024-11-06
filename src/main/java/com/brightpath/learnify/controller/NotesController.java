package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.NotesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.note.Note;
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
                dtoMapper.asNoteType(noteCreateDto.getType())
        );
        return ResponseEntity.ok(dtoMapper.asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listRecentNotes() {
        //todo remember to do it per user when possible
        List<Note> noteSummaries = notesService.listRecentNotes();
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
        String content = notesService.getBoardNoteContentPage(noteId, pageNumber);
        return ResponseEntity.ok(dtoMapper.asBoardNotePageContentDto(content));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<DocumentNotePageDto> getDocumentNotePage(UUID noteId, Integer pageNumber) {
        String content = notesService.getDocumentNoteContentPage(noteId, pageNumber);
        return ResponseEntity.ok(dtoMapper.asDocumentNotePageDto(content));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<String> updateBoardNotePage(UUID noteId, Integer pageNumber, BoardNotePageDto boardNotePageDto) {
        notesService.updateBoardNoteContentPage(noteId, pageNumber, boardNotePageDto.getContent());
        return ResponseEntity.ok("Note updated");
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#noteId, 'NOTE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<String> updateDocumentNotePage(UUID noteId, Integer pageNumber, DocumentNotePageDto documentNotePageDto) {
        notesService.updateDocumentNoteContentPage(noteId, pageNumber, documentNotePageDto.getContent());
        return ResponseEntity.ok("Note updated");
    }
}
