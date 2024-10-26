package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.BindingsApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
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

import static com.brightpath.learnify.domain.common.ResourceType.NOTE;
import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BindingController implements BindingsApi {
    private final BindingService bindingService;
    private final DtoMapper dtoMapper;
    private final PermissionAccessService permissionAccessService;

    @Override
    public ResponseEntity<BindingDto> createBinding(BindingCreateDto bindingCreateDto) {
        permissionAccessService.checkUserPermissionToEditResource(bindingCreateDto.getNoteId(), NOTE);
        permissionAccessService.checkUserPermissionToEditResource(bindingCreateDto.getQuizId(), QUIZ);
        Binding binding = bindingService.createBinding(bindingCreateDto.getNoteId(), bindingCreateDto.getQuizId());
        return ResponseEntity.ok(dtoMapper.asBindingDto(binding));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listNotesBoundToQuiz(UUID quizId) {
        permissionAccessService.checkUserPermissionToViewResource(quizId, QUIZ);
        List<Note> notes = bindingService.listNotesBoundToQuiz(quizId);
        return ResponseEntity.ok(notes.stream().map(dtoMapper::asNoteSummaryDto).toList());
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listQuizzesBoundToNote(UUID noteId) {
        permissionAccessService.checkUserPermissionToViewResource(noteId, NOTE);
        List<Quiz> quizzes = bindingService.listQuizzesBoundToNote(noteId);
        return ResponseEntity.ok(quizzes.stream().map(dtoMapper::asQuizSummaryDto).toList());
    }
}
