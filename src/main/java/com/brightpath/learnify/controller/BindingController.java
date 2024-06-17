package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.BindingsApi;
import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.model.BindingCreateDto;
import com.brightpath.learnify.model.BindingDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BindingController implements BindingsApi {

    private final BindingService bindingService;
    private final DtoMapper dtoMapper;

    @Override
    public ResponseEntity<BindingDto> createBinding(BindingCreateDto bindingCreateDto) {
        Binding binding = bindingService.createBinding(UUID.fromString(bindingCreateDto.getNoteId()), UUID.fromString(bindingCreateDto.getQuizId()));
        return ResponseEntity.ok(new BindingDto(binding.id().toString(), binding.noteId().toString(), binding.quizId().toString()));
    }

    @Override
    public ResponseEntity<List<NoteSummaryDto>> listNotesBoundToQuiz(String quizId) {
        List<Note> notes = bindingService.listNotesBoundToQuiz(UUID.fromString(quizId));
        return ResponseEntity.ok(notes.stream().map(dtoMapper::asNoteSummaryDto).toList());
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listQuizzesBoundToNote(String noteId) {
        List<com.brightpath.learnify.domain.quiz.Quiz> quizzes = bindingService.listQuizzesBoundToNote(UUID.fromString(noteId));
        return ResponseEntity.ok(quizzes.stream().map(dtoMapper::asQuizSummaryDto).toList());
    }
}
