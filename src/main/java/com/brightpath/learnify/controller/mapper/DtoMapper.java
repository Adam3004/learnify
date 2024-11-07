package com.brightpath.learnify.controller.mapper;

import com.brightpath.learnify.domain.auth.permission.FullResourcePermissionModel;
import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum;
import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteType;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionType;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.model.BindingDto;
import com.brightpath.learnify.model.BoardNotePageDto;
import com.brightpath.learnify.model.DocumentNotePageDto;
import com.brightpath.learnify.model.NoteSummaryDto;
import com.brightpath.learnify.model.NoteTypeDto;
import com.brightpath.learnify.model.PermissionSummaryDto;
import com.brightpath.learnify.model.QuestionCreationDto;
import com.brightpath.learnify.model.QuestionDto;
import com.brightpath.learnify.model.QuestionTypeDto;
import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizResultUpdateDto;
import com.brightpath.learnify.model.QuizSimpleResultDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.model.ResourceAccessTypeDto;
import com.brightpath.learnify.model.ResourceFullPermissionDto;
import com.brightpath.learnify.model.ResourceTypeDto;
import com.brightpath.learnify.model.UserAccessLevelDto;
import com.brightpath.learnify.model.UserSummaryDto;
import com.brightpath.learnify.model.UserSummaryWithAccessLevelDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import org.springframework.stereotype.Component;

import java.util.Map;
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
                .email(owner.email())
                .id(owner.id())
                .displayName(owner.displayName());
    }

    public WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace workspace) {
        return new WorkspaceSummaryDto()
                .id(workspace.id())
                .author(asUserSummaryDto(workspace.owner()))
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

    public ResourceType fromResourceTypeDto(ResourceTypeDto resourceTypeDto) {
        return switch (resourceTypeDto) {
            case NOTE -> ResourceType.NOTE;
            case QUIZ -> ResourceType.QUIZ;
            case WORKSPACE -> ResourceType.WORKSPACE;
        };
    }

    public ResourceTypeDto toResourceTypeDto(ResourceType resourceType) {
        return switch (resourceType) {
            case NOTE -> ResourceTypeDto.NOTE;
            case QUIZ -> ResourceTypeDto.QUIZ;
            case WORKSPACE -> ResourceTypeDto.WORKSPACE;
            case BOARD_NOTE_PAGE, DOCUMENT_NOTE_PAGE -> null;
        };
    }

    public ResourceAccessEnum fromAccessTypeDto(UserAccessLevelDto accessType) {
        return switch (accessType) {
            case RO -> ResourceAccessEnum.READ_ONLY;
            case RW -> ResourceAccessEnum.READ_WRITE;
        };
    }

    public UserAccessLevelDto toAccessTypeDto(ResourceAccessEnum resourceAccessEnum) {
        return switch (resourceAccessEnum) {
            case DENIED -> null;
            case READ_ONLY -> UserAccessLevelDto.RO;
            case OWNER, READ_WRITE -> UserAccessLevelDto.RW;
        };
    }

    public PermissionLevel fromResourceAccessTypeDto(ResourceAccessTypeDto resourceAccessTypeDto) {
        return switch (resourceAccessTypeDto) {
            case PUBLIC -> PermissionLevel.PUBLIC;
            case PRIVATE -> PermissionLevel.PRIVATE;
        };
    }

    public ResourceAccessTypeDto toResourceAccessTypeDto(PermissionLevel permissionLevel) {
        return switch (permissionLevel) {
            case PUBLIC -> ResourceAccessTypeDto.PUBLIC;
            case PRIVATE -> ResourceAccessTypeDto.PRIVATE;
        };
    }

    public PermissionSummaryDto toPermissionSummaryDto(Permission permission, UUID resourceId) {
        return new PermissionSummaryDto(permission.userId(), resourceId, toAccessTypeDto(permission.resourceAccessEnum()));
    }

    public UserSummaryWithAccessLevelDto toUserSummaryWithAccessLevelDto(Map.Entry<User, ResourceAccessEnum> entry) {
        return new UserSummaryWithAccessLevelDto()
                .user(asUserSummaryDto(entry.getKey()))
                .accessLevel(toAccessTypeDto(entry.getValue()));
    }

    public ResourceFullPermissionDto toResourceFullPermissionDto(FullResourcePermissionModel permissionsAccess) {
        return new ResourceFullPermissionDto()
                .accessType(toResourceAccessTypeDto(permissionsAccess.permissionLevel()))
                .permissions(permissionsAccess.userPermissions().entrySet().stream()
                        .map(this::toUserSummaryWithAccessLevelDto)
                        .toList())
                .resourceType(toResourceTypeDto(permissionsAccess.resourceType()))
                .resourceId(permissionsAccess.resourceId());
    }
}
