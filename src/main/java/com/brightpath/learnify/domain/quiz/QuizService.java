package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.question.QuestionRepository;
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
    private static final String ERROR_MESSAGE_CORRECT_AND_INCORRECT = "Sum of correct and incorrect answers wasn't equal to number of questions in quiz";
    private static final String ERROR_MESSAGE_INCORRECT_AND_INCORRECT_IDS = "Number of incorrect ids wasn't equal to number of incorrect ids";

    @PersistenceContext
    private EntityManager entityManager;
    private final QuizRepository quizRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final PermissionAccessService permissionAccessService;
    private final BindingService bindingService;
    private final QuestionRepository questionRepository;

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
                OffsetDateTime.now(Clock.systemUTC())
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

    public QuizSimpleResult updateQuizResult(UUID quizId, QuizSimpleResult quizSimpleResult, String userId, List<UUID> incorrectIds) {
        if (findQuizEntity(quizId).isEmpty()) {
            throw new ResourceNotFoundException(QUIZ);
        }
        if (quizSimpleResult == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        validateNumberOfAnswers(quiz, quizSimpleResult, incorrectIds);
        Optional<QuizResultsEntity> foundResults = quiz.getQuizResults().stream()
                .filter(result -> result.getUserId().equals(userId))
                .findFirst();
        QuizResultsEntity quizResultsForUser = foundResults.orElse(new QuizResultsEntity(uuidProvider.generateUuid(), userId, null, null, null, null, new HashSet<>()));
        quiz.getQuizResults().remove(quizResultsForUser);
        updateLastResults(quizResultsForUser, quizSimpleResult);
        Integer bestNumberOfCorrect = quizResultsForUser.getBestNumberOfCorrect();
        Integer bestNumberOfIncorrect = quizResultsForUser.getBestNumberOfIncorrect();
        if (quizSimpleResult.isGreaterThan(bestNumberOfCorrect, bestNumberOfIncorrect)) {
            updateBestResults(quizResultsForUser, quizSimpleResult);
        }
        updateQuizIncorrectQuestions(quizResultsForUser, incorrectIds, quizId);
        quiz.getQuizResults().add(quizResultsForUser);
        quizRepository.save(quiz);
        QuizSimpleResult quizSimpleResultToReturn = persistentMapper.asQuizSimpleResult(quizResultsForUser.getLastNumberOfCorrect(), quizResultsForUser.getLastNumberOfIncorrect());
        if (quizSimpleResultToReturn == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        return quizSimpleResultToReturn;
    }

    private void validateNumberOfAnswers(QuizEntity quiz, QuizSimpleResult quizSimpleResult, List<UUID> incorrectIds) {
        if (quizSimpleResult.correct() + quizSimpleResult.incorrect() != quiz.getNumberOfQuestions()) {
            throw new UpdatingQuizResultsFailedException(ERROR_MESSAGE_CORRECT_AND_INCORRECT);
        }
        if (quizSimpleResult.incorrect() != incorrectIds.size()) {
            throw new UpdatingQuizResultsFailedException(ERROR_MESSAGE_INCORRECT_AND_INCORRECT_IDS);
        }
    }

    private void updateQuizIncorrectQuestions(QuizResultsEntity quizResultsForUser, List<UUID> incorrectIds, UUID quizId) {
        if (lastIncorrectQuestionsAreTheSame(quizResultsForUser.getIncorrectQuestions(), incorrectIds)) {
            return;
        }
        quizResultsForUser.getIncorrectQuestions().clear();
        questionRepository.findAllByQuizId(quizId).stream()
                .filter(question -> incorrectIds.contains(question.getId()))
                .forEach(question -> quizResultsForUser.getIncorrectQuestions().add(question));
    }

    private boolean lastIncorrectQuestionsAreTheSame(Set<QuestionEntity> oldIncorrectQuestions, List<UUID> newIncorrectIds) {
        List<UUID> oldIds = oldIncorrectQuestions.stream()
                .map(QuestionEntity::getId)
                .toList();
        return oldIds.equals(newIncorrectIds);
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

    @Transactional
    public void deleteQuiz(UUID quizId) {
        permissionAccessService.deletePermissionToResource(quizId);
        bindingService.removeBindingForQuiz(quizId);
        quizRepository.deleteById(quizId);
    }
}
