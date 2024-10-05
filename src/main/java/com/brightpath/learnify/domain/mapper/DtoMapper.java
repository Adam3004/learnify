package com.brightpath.learnify.domain.mapper;

import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteType;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.model.BoardNotePageDto;
import com.brightpath.learnify.model.DocumentNotePageDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import com.brightpath.learnify.model.NoteTypeDto;
import com.brightpath.learnify.model.QuizResultUpdateDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.model.UserSummaryDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    public NoteSummaryDto asNoteSummaryDto(Note note) {
        return new NoteSummaryDto()
                .id(note.uuid())
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

    public BoardNotePageDto asBoardNotePageContentDto(String content) {
        return new BoardNotePageDto().content(content);
    }

    public DocumentNotePageDto asDocumentNotePageDto(String content) {
        return new DocumentNotePageDto().content(content);
    }

    public NoteType asNoteType(NoteTypeDto type) {
        return switch (type) {
            case BOARD -> NoteType.BOARD;
            case DOCUMENT -> NoteType.DOCUMENT;
        };
    }

    public UserSummaryDto asUserSummaryDto(User owner) {
        return new UserSummaryDto()
                .id(owner.id())
                .displayName(owner.displayName());
    }

    public WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace workspace) {
        return new WorkspaceSummaryDto()
                .id(workspace.id())
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
