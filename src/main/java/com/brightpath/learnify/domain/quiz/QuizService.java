package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.quiz.Quiz;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizRepository;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {
    @PersistenceContext
    private EntityManager entityManager;
    private final QuizRepository quizRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public Optional<Quiz> createQuiz(String title, String description, int numberOfQuestions, UUID workspaceId, UUID ownerId) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity author = entityManager.getReference(UserEntity.class, ownerId);
        QuizEntity quizEntity = new QuizEntity(uuidProvider.generateUuid(),
                workspace, title,
                description,
                numberOfQuestions,
                null,
                null,
                null,
                null,
                author,
                null);
        QuizEntity result = quizRepository.save(quizEntity);
        return Optional.of(persistentMapper.asQuiz(result));
    }

    public Optional<Quiz> showQuizById(UUID quizId) {
        Optional<QuizEntity> quizEntity = quizRepository.findById(quizId);
        if (quizEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(persistentMapper.asQuiz(quizEntity.get()));
    }
}
