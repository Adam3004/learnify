package com.brightpath.learnify.persistance.common;

import com.brightpath.learnify.domain.auth.permission.Permission;
import com.brightpath.learnify.domain.auth.permission.PermissionsAccess;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.persistance.auth.permissions.PermissionEntity;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.comment.CommentEntity;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentMapper {

    public Workspace asWorkspace(WorkspaceEntity entity) {
        return new Workspace(entity.getId(), entity.getDisplayName(), asUser(entity.getOwner()));
    }

    public User asUser(UserEntity entity) {
        return new User(entity.getId(), entity.getDisplayName(), entity.getEmail());
    }

    public Note asNote(NoteEntity entity) {
        return new Note(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                asWorkspace(entity.getWorkspace()),
                asUser(entity.getOwner()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getType()
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
        return new QuizSimpleResult(quizResults.getBestNumberOfIncorrect(), quizResults.getBestNumberOfCorrect());
    }

    private QuizSimpleResult asQuizSimpleLastResult(QuizResultsEntity quizResults) {
        if (quizResults.getLastNumberOfIncorrect() == null || quizResults.getLastNumberOfCorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(quizResults.getLastNumberOfIncorrect(), quizResults.getLastNumberOfCorrect());
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
                entity.getLastTryDate(),
                entity.getCreatedAt()
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

    public QuizSimpleResult asQuizSimpleResult(Integer correct, Integer incorrect) {
        if (correct == null || incorrect == null) {
            return null;
        }
        return new QuizSimpleResult(incorrect, correct);
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
                commentEntity.getOwnerId(),
                commentEntity.getResourceType(),
                commentEntity.getResourceId(),
                commentEntity.getRating(),
                commentEntity.getTitle(),
                commentEntity.getDescription());
    }
}
