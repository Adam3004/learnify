package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.BindingsApi;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToEditException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToGetException;
import com.brightpath.learnify.model.BindingCreateDto;
import com.brightpath.learnify.model.BindingDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_WRITE;
import static com.brightpath.learnify.domain.common.ResourceType.NOTE;
import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BindingController implements BindingsApi {

    private final BindingService bindingService;
    private final DtoMapper dtoMapper;
    private final UserIdentityService userIdentityService;
    private final PermissionAccessService permissionAccessService;

    @Override
    public ResponseEntity<BindingDto> createBinding(BindingCreateDto bindingCreateDto) {
        throwIfUserDoesNotHavePermissionToEditResource(bindingCreateDto.getNoteId(), NOTE);
        throwIfUserDoesNotHavePermissionToEditResource(bindingCreateDto.getQuizId(), QUIZ);
        Binding binding = bindingService.createBinding(bindingCreateDto.getNoteId(), bindingCreateDto.getQuizId());
        return ResponseEntity.ok(dtoMapper.asBindingDto(binding));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listNotesBoundToQuiz(UUID quizId) {
        throwIfUserDoesNotHavePermissionToViewResource(quizId, QUIZ);
        List<Note> notes = bindingService.listNotesBoundToQuiz(quizId);
        return ResponseEntity.ok(notes.stream().map(dtoMapper::asNoteSummaryDto).toList());
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listQuizzesBoundToNote(UUID noteId) {
        throwIfUserDoesNotHavePermissionToViewResource(noteId, NOTE);
        List<Quiz> quizzes = bindingService.listQuizzesBoundToNote(noteId);
        return ResponseEntity.ok(quizzes.stream().map(dtoMapper::asQuizSummaryDto).toList());
    }

    private void throwIfUserDoesNotHavePermissionToEditResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToEditNote = permissionAccessService.hasUserAccessToResource(userId, resourceId, resourceType, READ_WRITE);
        if (!hasAccessToEditNote) {
            throw new UserNotAuthorizedToEditException();
        }
    }

    private void throwIfUserDoesNotHavePermissionToViewResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToEditNote = permissionAccessService.hasUserAccessToResource(userId, resourceId, resourceType, READ_ONLY);
        if (!hasAccessToEditNote) {
            throw new UserNotAuthorizedToGetException();
        }
    }
}
