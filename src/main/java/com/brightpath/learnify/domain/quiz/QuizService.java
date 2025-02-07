package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.quiz.result.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.result.QuizUserResult;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.domain.quiz.port.QuizPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class QuizService {
    private static final String ERROR_MESSAGE_CORRECT_AND_INCORRECT = "Sum of correct and incorrect answers wasn't equal to number of questions in quiz";
    private static final String ERROR_MESSAGE_INCORRECT_AND_INCORRECT_IDS = "Number of incorrect ids wasn't equal to number of incorrect ids";

    private final PermissionAccessService permissionAccessService;
    private final QuizPersistencePort quizPersistencePort;

    public Optional<Quiz> createQuiz(String title, String description, UUID workspaceId, String ownerId, PermissionLevel permissionLevel) {
        return quizPersistencePort.createQuiz(title, description, workspaceId, ownerId, permissionLevel);
    }

    public Quiz showQuizById(UUID quizId, String userId) {
        return quizPersistencePort.getQuizById(quizId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(QUIZ));
    }

    public List<Quiz> listRecentQuizzes(String userId) {
        return quizPersistencePort.getRecentQuizzes(userId).stream()
                .filter(quiz -> permissionAccessService.checkUserPermissionToViewResource(quiz.id(), QUIZ))
                .toList();
    }

    public QuizSimpleResult updateQuizResult(UUID quizId, QuizSimpleResult quizSimpleResult, String userId, List<UUID> incorrectIds) {
        if (!quizPersistencePort.quizEntityExits(quizId)) {
            throw new ResourceNotFoundException(QUIZ);
        }
        if (quizSimpleResult == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        validateNumberOfAnswers(quizId, userId, quizSimpleResult, incorrectIds);
        QuizSimpleResult quizSimpleResultToReturn = quizPersistencePort.updateQuizResults(quizId, userId, quizSimpleResult, incorrectIds);
        if (quizSimpleResultToReturn == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        return quizSimpleResultToReturn;
    }

    private void validateNumberOfAnswers(UUID quizId, String userId, QuizSimpleResult quizSimpleResult, List<UUID> incorrectIds) {
        int numberOfQuestions = quizPersistencePort.getQuizById(quizId, userId).get().numberOfQuestions();
        if (quizSimpleResult.correct() + quizSimpleResult.incorrect() != numberOfQuestions) {
            throw new UpdatingQuizResultsFailedException(ERROR_MESSAGE_CORRECT_AND_INCORRECT);
        }
        if (quizSimpleResult.incorrect() != incorrectIds.size()) {
            throw new UpdatingQuizResultsFailedException(ERROR_MESSAGE_INCORRECT_AND_INCORRECT_IDS);
        }
    }

    public Quiz updateQuizDetailsById(UUID quizId, QuizCreationDto quizCreationDto, String userId) {
        if (!quizPersistencePort.quizEntityExits(quizId)) {
            throw new ResourceNotFoundException(QUIZ);
        }
        return quizPersistencePort.updateQuizDetails(quizId, quizCreationDto, userId);
    }

    public void deleteQuiz(UUID quizId) {
        quizPersistencePort.deleteQuiz(quizId);
    }

    public List<QuizUserResult> getTopQuizResults(UUID quizId, int numberOfExpectedResults) {
        return quizPersistencePort.getTopQuizResults(quizId, numberOfExpectedResults);
    }

    public List<Quiz> searchQuizzes(String userId, UUID workspaceId, String ownerId, String name, PermissionLevel permissionLevel, float averageRating) {
        return quizPersistencePort.searchQuizzes(userId, workspaceId, ownerId, name, permissionLevel, averageRating);
    }
}
