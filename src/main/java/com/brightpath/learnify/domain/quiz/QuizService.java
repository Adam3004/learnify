package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.comment.CommentEntity;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizRepository;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class QuizService {
    @PersistenceContext
    private EntityManager entityManager;
    private final QuizRepository quizRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final PermissionAccessService permissionAccessService;

    @Transactional
    public Optional<Quiz> createQuiz(String title, String description, UUID workspaceId, String ownerId, PermissionLevel permissionLevel) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity author = entityManager.getReference(UserEntity.class, ownerId);
        QuizEntity quizEntity = new QuizEntity(uuidProvider.generateUuid(),
                workspace,
                title,
                description,
                0,
                new HashSet<>(),
                author,
                null,
                OffsetDateTime.now(Clock.systemUTC()),
                new HashSet<>()
        );
        permissionAccessService.savePermissionAccess(quizEntity.getId(), QUIZ, ownerId, permissionLevel);
        QuizEntity result = quizRepository.save(quizEntity);
        return Optional.of(persistentMapper.asQuiz(result, ownerId));
    }

    public Quiz showQuizById(UUID quizId, String userId) {
        Optional<QuizEntity> quizEntity = findQuizEntity(quizId);
        return quizEntity.map(quiz -> persistentMapper.asQuiz(quiz, userId))
                .orElseThrow(() -> new ResourceNotFoundException(QUIZ));
    }

    public Optional<QuizEntity> findQuizEntity(UUID quizId) {
        return quizRepository.findById(quizId);
    }

    public List<Quiz> listRecentQuizzes(String userId) {
        List<QuizEntity> quizzes = quizRepository.findTop4RecentQuizzes();
        return quizzes.stream()
                .map(quiz -> persistentMapper.asQuiz(quiz, userId))
                .filter(quiz -> permissionAccessService.checkUserPermissionToViewResource(quiz.id(), QUIZ))
                .toList();
    }

    public QuizSimpleResult updateQuizResult(UUID quizId, QuizSimpleResult quizSimpleResult, String userId) {
        if (findQuizEntity(quizId).isEmpty()) {
            throw new ResourceNotFoundException(QUIZ);
        }
        if (quizSimpleResult == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        Optional<QuizResultsEntity> foundResults = quiz.getQuizResults().stream()
                .filter(result -> result.getUserId().equals(userId))
                .findFirst();
        QuizResultsEntity quizResultsForUser = foundResults.orElse(new QuizResultsEntity(uuidProvider.generateUuid(), userId, null, null, null, null));
        quiz.getQuizResults().remove(quizResultsForUser);
        updateLastResults(quizResultsForUser, quizSimpleResult);
        Integer bestNumberOfCorrect = quizResultsForUser.getBestNumberOfCorrect();
        Integer bestNumberOfIncorrect = quizResultsForUser.getBestNumberOfIncorrect();
        if (quizSimpleResult.isGreaterThan(bestNumberOfCorrect, bestNumberOfIncorrect)) {
            updateBestResults(quizResultsForUser, quizSimpleResult);
        }
        quiz.getQuizResults().add(quizResultsForUser);
        quizRepository.save(quiz);
        QuizSimpleResult quizSimpleResultToReturn = persistentMapper.asQuizSimpleResult(quizResultsForUser.getLastNumberOfCorrect(), quizResultsForUser.getLastNumberOfIncorrect());
        if (quizSimpleResultToReturn == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        return quizSimpleResultToReturn;
    }

    public Comment addCommentToQuiz(CommentCreation newComment, UUID quizId) {
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        Set<CommentEntity> quizComments = quiz.getComments();
        CommentEntity newCommentEntity = new CommentEntity(uuidProvider.generateUuid(),
                newComment.commentOwnerId(),
                newComment.rating(),
                newComment.title().orElse(null),
                newComment.description().orElse(null));
        quizComments.add(newCommentEntity);
        quiz.setComments(quizComments);
        quizRepository.save(quiz);
        return persistentMapper.asComment(newCommentEntity);
    }

    public void updateQuiz(QuizEntity quiz) {
        quizRepository.save(quiz);
    }

    private void updateLastResults(QuizResultsEntity quizResults, QuizSimpleResult quizSimpleResult) {
        quizResults.setLastNumberOfCorrect(quizSimpleResult.correct());
        quizResults.setLastNumberOfIncorrect(quizSimpleResult.incorrect());
    }

    private void updateBestResults(QuizResultsEntity quizResults, QuizSimpleResult quizSimpleResult) {
        quizResults.setBestNumberOfCorrect(quizSimpleResult.correct());
        quizResults.setBestNumberOfIncorrect(quizSimpleResult.incorrect());
    }

    public List<Quiz> listQuizzes(String userId, @Nullable UUID workspaceId) {
        List<QuizEntity> quizzes = quizRepository.searchQuizzes(workspaceId);
        return quizzes.stream()
                .map(quiz -> persistentMapper.asQuiz(quiz, userId))
                .filter(quiz -> permissionAccessService.hasUserAccessToResource(userId, quiz.id(), QUIZ, READ_ONLY))
                .toList();
    }
}
