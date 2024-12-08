package com.brightpath.learnify.persistance.common;

import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionsAccess;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.result.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.result.QuizUserResult;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.persistance.auth.permissions.PermissionEntity;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.comment.CommentEntity;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.note.date.DateStatisticsEntity;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class PersistentMapper {

    public Workspace asWorkspace(WorkspaceEntity entity) {
        return new Workspace(entity.getId(), entity.getDisplayName(), asUser(entity.getOwner()), getParentWorkspaceId(entity.getParentWorkspace()), asListOfUUIDS(entity.getSubWorkspaces()));
    }

    private UUID getParentWorkspaceId(WorkspaceEntity parentWorkspace) {
        if (parentWorkspace == null) {
            return null;
        }
        return parentWorkspace.getId();
    }

    private List<UUID> asListOfUUIDS(Set<WorkspaceEntity> subWorkspaces) {
        return subWorkspaces.stream()
                .map(WorkspaceEntity::getId)
                .toList();
    }

    public User asUser(UserEntity entity) {
        return new User(entity.getId(), entity.getDisplayName(), entity.getEmail());
    }

    public Note asNote(NoteEntity entity, String userId) {
        Optional<DateStatisticsEntity> foundStatistics = entity.getDateStatistics().stream()
                .filter(dateStatistics -> dateStatistics.getUserId().equals(userId))
                .findAny();
        return new Note(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                asWorkspace(entity.getWorkspace()),
                asUser(entity.getOwner()),
                entity.getCreatedAt(),
                foundStatistics.map(DateStatisticsEntity::getUpdatedAt).orElse(null),
                foundStatistics.map(DateStatisticsEntity::getViewedAt).orElse(null),
                entity.getType(),
                entity.getPagesCount(),
                entity.getPermissionsAccess().getPermissionLevel()
        );
    }

    public QuizSimpleResult asBestSimpleResult(QuizEntity entity, String userId) {
        return entity.getQuizResults().stream()
                .filter(results -> results.getUserId().equals(userId))
                .findFirst()
                .map(this::asQuizSimpleBestResult)
                .orElse(null);
    }

    public QuizSimpleResult asLastSimpleResult(QuizEntity entity, String userId) {
        return entity.getQuizResults().stream()
                .filter(results -> results.getUserId().equals(userId))
                .findFirst()
                .map(this::asQuizSimpleLastResult)
                .orElse(null);
    }

    private QuizSimpleResult asQuizSimpleBestResult(QuizResultsEntity quizResults) {
        if (quizResults.getBestNumberOfIncorrect() == null || quizResults.getBestNumberOfCorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(quizResults.getBestNumberOfIncorrect(), quizResults.getBestNumberOfCorrect(), quizResults.getBestTryDate());
    }

    private QuizSimpleResult asQuizSimpleLastResult(QuizResultsEntity quizResults) {
        if (quizResults.getLastNumberOfIncorrect() == null || quizResults.getLastNumberOfCorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(quizResults.getLastNumberOfIncorrect(), quizResults.getLastNumberOfCorrect(), quizResults.getLastTryDate());
    }

    public Quiz asQuiz(QuizEntity entity, String userId) {
        return new Quiz(
                entity.getId(),
                asWorkspace(entity.getWorkspace()),
                entity.getTitle(),
                entity.getDescription(),
                entity.getNumberOfQuestions(),
                asLastSimpleResult(entity, userId),
                asBestSimpleResult(entity, userId),
                asUser(entity.getAuthor()),
                entity.getCreatedAt(),
                entity.getPermissionsAccess().getPermissionLevel()
        );
    }

    public Question asQuestion(QuestionEntity entity) {
        return new Question(entity.getId(),
                entity.getQuestion(),
                entity.getType(),
                entity.getQuizId(),
                entity.getWeight(),
                entity.getChoices(),
                entity.getFeedback(),
                entity.getOtherProperties());
    }

    public List<Question> asQuestions(List<QuestionEntity> savedEntities) {
        return savedEntities.stream()
                .map(this::asQuestion)
                .toList();
    }

    public QuizSimpleResult asQuizSimpleResult(Integer correct, Integer incorrect, OffsetDateTime tryDate) {
        if (correct == null || incorrect == null) {
            return null;
        }
        return new QuizSimpleResult(incorrect, correct, tryDate);
    }

    public Permission asPermission(PermissionEntity entity) {
        return new Permission(entity.getUserId(), entity.getAccess());
    }

    public PermissionsAccess asPermissionsAccess(PermissionsAccessEntity permissionsAccessEntity) {
        return new PermissionsAccess(
                permissionsAccessEntity.getPermissionLevel(),
                permissionsAccessEntity.getPermissions().stream()
                        .map(this::asPermission)
                        .toList(),
                permissionsAccessEntity.getResourceType(),
                permissionsAccessEntity.getResourceId()
        );
    }

    public Comment asComment(CommentEntity commentEntity) {
        return new Comment(commentEntity.getId(),
                asUser(commentEntity.getOwner()),
                commentEntity.getResourceType(),
                commentEntity.getResourceId(),
                commentEntity.getRating(),
                commentEntity.getTitle(),
                commentEntity.getDescription());
    }

    public QuizUserResult asQuizUserResult(QuizResultsEntity quizResults, String userName) {
        int percentage = 100;
        if (quizResults.getBestNumberOfIncorrect() > 0) {
            percentage = (int) (100.0 * quizResults.getBestNumberOfCorrect() / quizResults.getBestNumberOfIncorrect());
        }
        return new QuizUserResult(userName,
                percentage,
                quizResults.getBestTryDate());
    }
}
