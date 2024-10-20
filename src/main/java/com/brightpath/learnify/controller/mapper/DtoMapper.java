package com.brightpath.learnify.controller.mapper;

import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteType;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionType;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

    public QuestionTypeDto asQuestionTypeDto(QuestionType type) {
        return switch (type) {
            case MULTIPLE_CHOICE -> QuestionTypeDto.MULTIPLE_CHOICE;
            case SINGLE_CHOICE -> QuestionTypeDto.SINGLE_CHOICE;
        };
    }

    public QuestionDto asQuestionDto(Question givenQuestion) {
        return new QuestionDto()
                .quizId(givenQuestion.id())
                .question(givenQuestion.question())
                .type(asQuestionTypeDto(givenQuestion.type()))
                .quizId(givenQuestion.quizId())
                .weight(givenQuestion.weight())
                .choices(givenQuestion.choices())
                .feedback(givenQuestion.feedback())
                .otherProperties(givenQuestion.otherProperties());
    }

    public QuizSummaryDto asQuizSummaryDto(Quiz quiz) {
        return new QuizSummaryDto()
                .id(quiz.id())
                .workspace(asWorkspaceSummaryDto(quiz.workspace()))
                .title(quiz.title())
                .score(quiz.findScore())
                .author(asUserSummaryDto(quiz.author()));
    }

    public QuizSimpleResult asQuizSimpleResult(QuizResultUpdateDto quizResultUpdateDto) {
        if (quizResultUpdateDto.getCorrect() == null || quizResultUpdateDto.getIncorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(quizResultUpdateDto.getIncorrect(), quizResultUpdateDto.getCorrect());
    }

    public QuizResultUpdateDto asQuizResultUpdateDto(QuizSimpleResult quizResultUpdateDto) {
        return new QuizResultUpdateDto()
                .correct(quizResultUpdateDto.correct())
                .incorrect(quizResultUpdateDto.incorrect());
    }

    public BindingDto asBindingDto(Binding binding) {
        return new BindingDto()
                .bindingId(binding.id())
                .noteId(binding.noteId())
                .quizId(binding.quizId());
    }

    public QuizSimpleResultDto asQuizSimpleResultDto(QuizSimpleResult quizSimpleResult) {
        return new QuizSimpleResultDto()
                .correct(quizSimpleResult.correct())
                .incorrect(quizSimpleResult.incorrect());
    }

    public QuizDetailsDto asQuizDetailsDto(Quiz quiz) {
        return new QuizDetailsDto()
                .id(quiz.id())
                .workspace(asWorkspaceSummaryDto(quiz.workspace()))
                .title(quiz.title())
                .description(quiz.description())
                .numberOfQuestions(quiz.numberOfQuestions())
                .author(asUserSummaryDto(quiz.author()))
                .createdAt(quiz.createdAt())
                .bestScore(quiz.bestScore() == null ? null : asQuizSimpleResultDto(quiz.bestScore()))
                .lastScore(quiz.lastScore() == null ? null : asQuizSimpleResultDto(quiz.lastScore()))
                .lastTryDate(quiz.lastTryDate());
    }

    public QuestionType asQuestionType(QuestionTypeDto type) {
        return switch (type) {
            case MULTIPLE_CHOICE -> QuestionType.MULTIPLE_CHOICE;
            case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
        };
    }

    public Question fromQuestionCreationDto(QuestionCreationDto currentDto, UUID quizId) {
        return Question.builder()
                .id(null)
                .question(currentDto.getQuestion())
                .type(asQuestionType(currentDto.getType()))
                .quizId(quizId)
                .weight(currentDto.getWeight())
                .choices(currentDto.getChoices())
                .feedback(currentDto.getFeedback())
                .otherProperties(currentDto.getOtherProperties())
                .build();
    }

    public Question fromQuestionDto(UUID id, QuestionDto currentDto, UUID quizId) {
        return Question.builder()
                .id(id)
                .question(currentDto.getQuestion())
                .type(asQuestionType(currentDto.getType()))
                .quizId(quizId)
                .weight(currentDto.getWeight())
                .choices(currentDto.getChoices())
                .feedback(currentDto.getFeedback())
                .otherProperties(currentDto.getOtherProperties())
                .build();
    }
}
