package com.brightpath.learnify.controller;

import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteType;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.model.*;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    public NoteSummaryDto asNoteSummaryDto(Note note) {
        return new NoteSummaryDto()
                .id(note.uuid().toString())
                .title(note.title())
                .description(note.description())
                .workspace(asWorkspaceSummaryDto(note.workspace()))
                .author(asUserSummaryDto(note.owner()))
                .createdAt(note.createdAt())
                .type(asNoteTypeDto(note.type()))
                .updatedAt(note.updatedAt());
    }

    public NoteTypeDto asNoteTypeDto(NoteType type) {
        return switch (type) {
            case BOARD -> NoteTypeDto.BOARD;
            case DOCUMENT -> NoteTypeDto.DOCUMENT;
        };
    }

    public NotePageContentDto asNotePageContentDto(String content) {
        return new NotePageContentDto()
                .content(content);
    }

    public NoteType asNoteType(NoteTypeDto type) {
        return switch (type) {
            case BOARD -> NoteType.BOARD;
            case DOCUMENT -> NoteType.DOCUMENT;
        };
    }

    public UserSummaryDto asUserSummaryDto(User owner) {
        return new UserSummaryDto()
                .id(owner.id().toString())
                .displayName(owner.displayName());
    }

    public WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace workspace) {
        return new WorkspaceSummaryDto()
                .id(workspace.id().toString())
                .displayName(workspace.displayName());
    }

    public QuizSummaryDto asQuizSummaryDto(Quiz quiz) {
        return quiz.convertToQuizSummaryDto();
    }

    public QuizSimpleResult asQuizSimpleResult(QuizResultUpdateDto quizResultUpdateDto) {
        if (quizResultUpdateDto.getCorrect() == null || quizResultUpdateDto.getIncorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(quizResultUpdateDto.getIncorrect(), quizResultUpdateDto.getCorrect());
    }

    public QuizResultUpdateDto asQuizResultUpdateDto(QuizSimpleResult quizResultUpdateDto) {

        return new QuizResultUpdateDto(quizResultUpdateDto.correct(), quizResultUpdateDto.incorrect());
    }
}
