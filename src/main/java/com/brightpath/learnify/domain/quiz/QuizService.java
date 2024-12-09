package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.binding.BindingService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.result.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.result.QuizUserResult;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.persistance.auth.permissions.PermissionsAccessEntity;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.common.RatingsEmbeddableEntity;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.question.QuestionRepository;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizRepository;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final UserService userService;

    @Transactional
    public Optional<Quiz> createQuiz(String title, String description, UUID workspaceId, String ownerId, PermissionLevel permissionLevel) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity author = entityManager.getReference(UserEntity.class, ownerId);
        UUID quizId = uuidProvider.generateUuid();
        PermissionsAccessEntity permissionsAccessEntity = permissionAccessService.savePermissionAccess(quizId, QUIZ, ownerId, permissionLevel);
        RatingsEmbeddableEntity ratings = new RatingsEmbeddableEntity(0, 0, 0);
        QuizEntity quizEntity = new QuizEntity(quizId,
                workspace,
                title,
                description,
                0,
                new HashSet<>(),
                author,
                OffsetDateTime.now(Clock.systemUTC()),
                permissionsAccessEntity,
                ratings
        );

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
        Pageable pageable = PageRequest.of(0, 4);
        List<QuizEntity> quizzes = quizRepository.findTop4RecentQuizzes(userId, pageable);
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
        QuizResultsEntity quizResultsForUser = foundResults.orElse(new QuizResultsEntity(uuidProvider.generateUuid(), userId, null, null, null, null, new HashSet<>(), null, null));
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
        QuizSimpleResult quizSimpleResultToReturn = persistentMapper.asQuizSimpleResult(quizResultsForUser.getLastNumberOfCorrect(), quizResultsForUser.getLastNumberOfIncorrect(), quizResultsForUser.getLastTryDate());
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

    public Quiz updateQuizDetailsById(UUID quizId, QuizCreationDto quizCreationDto, String userId) {
        if (findQuizEntity(quizId).isEmpty()) {
            throw new ResourceNotFoundException(QUIZ);
        }
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        if (quizCreationDto.getWorkspaceId() != null) {
            WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, quizCreationDto.getWorkspaceId());
            quiz.setWorkspace(workspace);
        }
        if (quizCreationDto.getTitle() != null) {
            quiz.setTitle(quizCreationDto.getTitle());
        }
        if (quizCreationDto.getDescription() != null) {
            quiz.setDescription(quizCreationDto.getDescription());
        }
        QuizEntity savedQuiz = quizRepository.save(quiz);
        return persistentMapper.asQuiz(savedQuiz, userId);
    }

    public void updateQuiz(QuizEntity quiz) {
        quizRepository.save(quiz);
    }

    private void updateLastResults(QuizResultsEntity quizResults, QuizSimpleResult quizSimpleResult) {
        quizResults.setLastNumberOfCorrect(quizSimpleResult.correct());
        quizResults.setLastNumberOfIncorrect(quizSimpleResult.incorrect());
        quizResults.setLastTryDate(quizSimpleResult.tryDate());
    }

    private void updateBestResults(QuizResultsEntity quizResults, QuizSimpleResult quizSimpleResult) {
        quizResults.setBestNumberOfCorrect(quizSimpleResult.correct());
        quizResults.setBestNumberOfIncorrect(quizSimpleResult.incorrect());
        quizResults.setBestTryDate(quizSimpleResult.tryDate());
    }

    @Transactional
    public void deleteQuiz(UUID quizId) {
        permissionAccessService.deletePermissionToResource(quizId);
        bindingService.removeBindingForQuiz(quizId);
        quizRepository.deleteById(quizId);
    }

    public List<QuizUserResult> getTopQuizResults(UUID quizId, int numberOfExpectedResults) {
        QuizEntity quiz = quizRepository.getReferenceById(quizId);
        List<QuizResultsEntity> topResults = quiz.getQuizResults().stream()
                .sorted(Comparator.comparing(QuizResultsEntity::getBestNumberOfCorrect).reversed().thenComparing(QuizResultsEntity::getBestTryDate))
                .limit(numberOfExpectedResults)
                .toList();
        List<String> topUserIds = topResults.stream()
                .map(QuizResultsEntity::getUserId)
                .toList();
        Map<String, String> userIdsToUserNames = userService.getUsersByIds(topUserIds).stream()
                .collect(Collectors.toMap(User::id, User::displayName));
        return topResults.stream()
                .map(result -> persistentMapper.asQuizUserResult(result, userIdsToUserNames.get(result.getUserId())))
                .toList();
    }

    public List<Quiz> searchQuizzes(String userId, UUID workspaceId, String ownerId, String name, PermissionLevel permissionLevel, float averageRating) {
        String titleFilter = Optional.ofNullable(name).map(String::toLowerCase).orElse("");
        List<QuizEntity> quizzes = quizRepository.searchQuizzes(userId, workspaceId, ownerId, titleFilter, permissionLevel, averageRating);
        return quizzes.stream()
                .map(quiz -> persistentMapper.asQuiz(quiz, userId))
                .toList();
    }
}
