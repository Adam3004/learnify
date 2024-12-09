package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.badrequest.UpdatingQuizResultsFailedException;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizRepository;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brightpath.learnify.exception.notfound.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class QuizService {
    @PersistenceContext
    private EntityManager entityManager;
    private final QuizRepository quizRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public Optional<Quiz> createQuiz(String title, String description, UUID workspaceId, UUID ownerId) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity author = entityManager.getReference(UserEntity.class, ownerId);
        QuizEntity quizEntity = new QuizEntity(uuidProvider.generateUuid(),
                workspace,
                title,
                description,
                0,
                null,
                null,
                null,
                null,
                author,
                null,
                OffsetDateTime.now(Clock.systemUTC())
        );
        QuizEntity result = quizRepository.save(quizEntity);
        return Optional.of(persistentMapper.asQuiz(result));
    }

    public Quiz showQuizById(UUID quizId) {
        Optional<QuizEntity> quizEntity = findQuizEntity(quizId);
        return quizEntity.map(persistentMapper::asQuiz).orElseThrow(() -> new ResourceNotFoundException(QUIZ));
    }

    public Optional<QuizEntity> findQuizEntity(UUID quizId) {
        return quizRepository.findById(quizId);
    }

    public List<Quiz> listRecentQuizzes() {
        List<QuizEntity> quizzes = quizRepository.findTop4RecentQuizzes();
        return quizzes.stream()
                .map(persistentMapper::asQuiz)
                .toList();
    }

    public QuizSimpleResult updateQuizResult(UUID quizId, QuizSimpleResult quizSimpleResult) {
        if (findQuizEntity(quizId).isEmpty()) {
            throw new ResourceNotFoundException(QUIZ);
        }
        if (quizSimpleResult == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        updateLastResults(quiz, quizSimpleResult);
        Integer bestNumberOfCorrect = quiz.getBestNumberOfCorrect();
        Integer bestNumberOfIncorrect = quiz.getBestNumberOfIncorrect();
        if (quizSimpleResult.isGreaterThan(bestNumberOfCorrect, bestNumberOfIncorrect)) {
            updateBestResults(quiz, quizSimpleResult);
        }
        QuizEntity savedQuiz = quizRepository.save(quiz);
        QuizSimpleResult quizSimpleResultToReturn = persistentMapper.asQuizSimpleResult(savedQuiz.getLastNumberOfCorrect(), savedQuiz.getLastNumberOfIncorrect());
        if (quizSimpleResultToReturn == null) {
            throw new UpdatingQuizResultsFailedException();
        }
        return quizSimpleResultToReturn;
    }

    public Quiz updateQuizDetailsById(UUID quizId, QuizCreationDto quizCreationDto) {
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
        return persistentMapper.asQuiz(savedQuiz);
    }

    public void updateQuiz(QuizEntity quiz) {
        quizRepository.save(quiz);
    }

    private void updateLastResults(QuizEntity quiz, QuizSimpleResult quizSimpleResult) {
        quiz.setLastNumberOfCorrect(quizSimpleResult.correct());
        quiz.setLastNumberOfIncorrect(quizSimpleResult.incorrect());
    }

    private void updateBestResults(QuizEntity quiz, QuizSimpleResult quizSimpleResult) {
        quiz.setBestNumberOfCorrect(quizSimpleResult.correct());
        quiz.setBestNumberOfIncorrect(quizSimpleResult.incorrect());
    }
}
