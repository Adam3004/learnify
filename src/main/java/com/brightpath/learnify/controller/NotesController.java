package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.NotesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToEditException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToGetException;
import com.brightpath.learnify.model.BoardNotePageDto;
import com.brightpath.learnify.model.DocumentNotePageDto;
import com.brightpath.learnify.model.NoteCreateDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_WRITE;
import static com.brightpath.learnify.domain.common.ResourceType.NOTE;
import static com.brightpath.learnify.domain.common.ResourceType.WORKSPACE;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotesController implements NotesApi {

    private final NoteService notesService;
    private final UserIdentityService userIdentityService;
    private final DtoMapper dtoMapper;
    private final PermissionAccessService permissionAccessService;

    @Override
    public ResponseEntity<NoteSummaryDto> getNoteById(UUID noteId) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToReadNote = permissionAccessService.hasUserAccessToResource(userId, noteId, NOTE, READ_ONLY);
        if(!hasAccessToReadNote) {
            throw new UserNotAuthorizedToGetException();
        }
        Note note = notesService.getNoteById(noteId);
        return ResponseEntity.ok(dtoMapper.asNoteSummaryDto(note));
    }

    @Override
    public ResponseEntity<NoteSummaryDto> createNote(NoteCreateDto noteCreateDto) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToWorkspace = permissionAccessService.hasUserAccessToResource(userId, noteCreateDto.getWorkspaceId(), WORKSPACE, READ_WRITE);
        if(!hasAccessToWorkspace) {
            throw new UserNotAuthorizedToEditException();
        }
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
    public ResponseEntity<BoardNotePageDto> getBoardNotePage(UUID noteId, Integer pageNumber) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToReadNote = permissionAccessService.hasUserAccessToResource(userId, noteId, NOTE, READ_ONLY);
        if(!hasAccessToReadNote) {
            throw new UserNotAuthorizedToGetException();
        }
        String content = notesService.getBoardNoteContentPage(noteId, pageNumber);
        return ResponseEntity.ok(dtoMapper.asBoardNotePageContentDto(content));
    }

    @Override
    public ResponseEntity<DocumentNotePageDto> getDocumentNotePage(UUID noteId, Integer pageNumber) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToReadNote = permissionAccessService.hasUserAccessToResource(userId, noteId, NOTE, READ_ONLY);
        if(!hasAccessToReadNote) {
            throw new UserNotAuthorizedToGetException();
        }
        String content = notesService.getDocumentNoteContentPage(noteId, pageNumber);
        return ResponseEntity.ok(dtoMapper.asDocumentNotePageDto(content));
    }

    @Override
    public ResponseEntity<String> updateBoardNotePage(UUID noteId, Integer pageNumber, BoardNotePageDto boardNotePageDto) {
        notesService.updateBoardNoteContentPage(noteId, pageNumber, boardNotePageDto.getContent());
        return ResponseEntity.ok("Note updated");
    }

    @Override
    public ResponseEntity<String> updateDocumentNotePage(UUID noteId, Integer pageNumber, DocumentNotePageDto documentNotePageDto) {
        notesService.updateBoardNoteContentPage(noteId, pageNumber, documentNotePageDto.getContent());
        return ResponseEntity.ok("Note updated");
    }
}
